package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.decorator.*;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO;

@Service
public class ActionProcessingService {
  private static final Logger log = LoggerFactory.getLogger(ActionProcessingService.class);

  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  public static final String CANCELLATION_REASON = "Action cancelled by Response Management";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";
  public static final String ENABLED = "ENABLED";
  public static final String NOTIFY = "Notify";
  public static final String PRINTER = "Printer";
  public static final String PENDING = "PENDING";

  @Autowired private ActionRepository actionRepo;

  @Autowired private ActionCaseRepository actionCaseRepo;

  @Autowired private NotifyService notifyService;

  @Autowired private NotificationFileCreator notificationFileCreator;

  @Autowired
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  @Autowired private ActionRequestContextFactory decoratorContextFactory;

  private static final ActionRequestDecorator[] DECORATORS = {
    new ActionAndActionPlan(),
    new CaseAndCaseEvent(),
    new CollectionExerciseAndSurvey(),
    new PartyAndContact(),
    new SampleUnitRef()
  };

  public void processActions(ActionType actionType, List<Action> allActions) {
    if (actionType.getHandler().equals(NOTIFY)) {
      processEmails(actionType, allActions);
    } else if (actionType.getHandler().equals(PRINTER)) {
      processLetters(actionType, allActions);
    } else {
      log.with("handler", actionType.getHandler()).warn("unsupported action type handler");
    }
  }

  public void processLetters(ActionType actionType, List<Action> allActions) {
    // action requests are decorated actions
    List<ActionRequest> printerActions = new ArrayList<>();
    for (Action action : allActions) {
      if (checkCaseExists(action) && !cancelled(action)) {
        transitionAction(action, actionType);
        List<ActionRequest> actionRequests = prepareActionRequests(action);
        printerActions.addAll(actionRequests);
      }
    }
    notificationFileCreator.export(printerActions);
  }

  public void processEmails(ActionType actionType, List<Action> allActions) {
    for (Action action : allActions) {
      if (checkCaseExists(action) && !cancelled(action)) {
        transitionAction(action, actionType);
        // send to pub sub
        List<ActionRequest> actionRequests = prepareActionRequests(action);
        for (ActionRequest actionRequest : actionRequests) {
          notifyService.processNotification(actionRequest);
        }
      }
    }
  }

  private boolean cancelled(final Action action) {
    return action.getState() == ActionDTO.ActionState.CANCELLED;
  }

  private boolean checkCaseExists(final Action action) {
    ActionCase actionCase = actionCaseRepo.findById(action.getCaseId());

    if (actionCase == null) {
      log.with("action", action).info("Case no longer exists for action");
      ActionDTO.ActionState newActionState;
      try {
        newActionState =
            actionSvcStateTransitionManager.transition(
                action.getState(), ActionDTO.ActionEvent.REQUEST_CANCELLED);
      } catch (CTPException ex) {
        throw new IllegalStateException(ex);
      }

      action.setState(newActionState);
      actionRepo.saveAndFlush(action);
      return false;
    }
    return true;
  }

  /**
   * Change the action status in db to indicate we have sent this action downstream, and clear
   * previous situation (in the scenario where the action has prev. failed)
   *
   * @param action the action to change and persist
   * @param actionType the action type
   * @throws CTPException if action state transition error
   */
  private void transitionAction(final Action action, final ActionType actionType) {

    ActionDTO.ActionEvent event =
        actionType.getResponseRequired()
            ? ActionDTO.ActionEvent.REQUEST_DISTRIBUTED
            : ActionDTO.ActionEvent.REQUEST_COMPLETED;

    ActionDTO.ActionState nextState = null;

    try {
      nextState = actionSvcStateTransitionManager.transition(action.getState(), event);
    } catch (CTPException ctpExeption) {
      throw new IllegalStateException(ctpExeption);
    }

    action.setState(nextState);
    action.setSituation(null);
    action.setUpdatedDateTime(DateTimeUtil.nowUTC());
    actionRepo.saveAndFlush(action);
  }

  public List<ActionRequest> prepareActionRequests(Action action) {
    final ActionRequestContext context =
        decoratorContextFactory.getActionRequestDecoratorContext(action);

    // If action is sampleUnitType B and handler type NOTIFY
    // then create an action request per respondent
    log.with("actionId", action.getId()).info("Setting action request array for processing");
    ArrayList<ActionRequest> actionRequests = new ArrayList<>();
    if (isBusinessNotification(context)) {
      context
          .getChildParties()
          .forEach(
              p -> {
                log.with("actionId", action.getId())
                    .info("Creating Action request for pubsub notify");
                context.setChildParties(Collections.singletonList(p));
                ActionRequest actionRequest = prepareActionRequest(context);
                // actionRequest.setIsPubsub(true);
                actionRequests.add(actionRequest);
                log.with("actionId", action.getId()).info("Pubsub notify action added to the list");
              });
    } else {
      ActionRequest actionRequest = prepareActionRequest(context);
      actionRequests.add(actionRequest);
    }
    return actionRequests;
  }

  private boolean isBusinessNotification(ActionRequestContext context) {
    return (context
            .getCaseDetails()
            .getSampleUnitType()
            .equals(SampleUnitDTO.SampleUnitType.B.name())
        && context.getAction().getActionType().getHandler().equals(NOTIFY));
  }

  private ActionRequest prepareActionRequest(ActionRequestContext context) {
    ActionRequest actionRequest = new ActionRequest();
    Arrays.stream(this.DECORATORS).forEach(d -> d.decorateActionRequest(actionRequest, context));
    return actionRequest;
  }
}
