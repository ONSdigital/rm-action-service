package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.decorator.*;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO;

@Service
public class ActionProcessingService {
  private static final Logger log = LoggerFactory.getLogger(ActionProcessingService.class);
  public static final String ACTION_TYPE_NOT_DEFINED = "ActionType is not defined for action";
  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";
  public static final String ENABLED = "ENABLED";
  public static final String PENDING = "PENDING";
  private static final String NOTIFY = "Notify";
  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;

  @Autowired private ActionCaseRepository actionCaseRepo;

  @Autowired private NotifyService notifyService;

  @Autowired private NotificationFileCreator notificationFileCreator;

  @Autowired private ActionStateService actionStateService;

  @Autowired private ActionRequestContextFactory decoratorContextFactory;

  private static final ActionRequestDecorator[] DECORATORS = {
    new ActionAndActionPlan(),
    new CaseAndCaseEvent(),
    new CollectionExerciseAndSurvey(),
    new PartyAndContact(),
    new SampleUnitRef()
  };

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS, propagation = Propagation.REQUIRES_NEW)
  public void processLetters(ActionType actionType, List<Action> allActions) {
    if (actionType == null) {
      throw new IllegalStateException(ACTION_TYPE_NOT_DEFINED);
    }
    // action requests are decorated actions
    log.with("name", actionType.getName())
        .with("actions", allActions.size())
        .debug("processing letters");
    List<ActionRequest> printerActions = new ArrayList<>();
    for (Action action : allActions) {
      if (isCancelled(action)) {
        processActionCancel(action);
      } else if (!checkCaseExists(action)) {
        cancelAction(action);
      } else if (valid(action)) {
        List<ActionRequest> actionRequests = prepareActionRequests(action);
        printerActions.addAll(actionRequests);
      } else {
        log.with("id", action.getId()).warn("skipping action");
      }
    }
    log.with("entries", printerActions.size()).debug("about to create print files");
    if (!printerActions.isEmpty()) {
      notificationFileCreator.export(getActionEvent(actionType), printerActions);
    }
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS, propagation = Propagation.REQUIRES_NEW)
  public void processEmails(ActionType actionType, List<Action> allActions) {
    if (actionType == null) {
      throw new IllegalStateException(ACTION_TYPE_NOT_DEFINED);
    }
    log.with("actions", allActions.size()).debug("processing emails");
    for (Action action : allActions) {
      if (isCancelled(action)) {
        processActionCancel(action);
      } else if (!checkCaseExists(action)) {
        cancelAction(action);
      } else if (valid(action)) {
        // send to pub sub
        List<ActionRequest> actionRequests = prepareActionRequests(action);
        try {
          for (ActionRequest actionRequest : actionRequests) {
            notifyService.processNotification(actionRequest);
          }
          // if successful transition action
          transitionAction(action, actionType);
        } catch (RuntimeException e) {
          // action will not be transition now and therefore will be picked up in the next run
          // so we don't need to do anything with the exception apart from log it.
          log.with("action id", action.getId()).error("Error sending email", e);
        }
      } else {
        log.with("id", action.getId()).warn("skipping action");
      }
    }
  }

  private boolean isCancelled(final Action action) {
    return action.getState() == ActionDTO.ActionState.CANCEL_SUBMITTED;
  }

  private boolean checkCaseExists(final Action action) {
    ActionCase actionCase = actionCaseRepo.findById(action.getCaseId());
    if (actionCase == null) {
      log.with("action", action).debug("Case no longer exists for action");
    }
    return actionCase != null;
  }

  /**
   * This method will take an action and collect all the additional information relating to that
   * action from other services, such as party, collection exercise. It uses a decorator pattern to
   * do this.
   *
   * @param action the action to add information too
   * @return the action request which holds all the additional information.
   */
  protected List<ActionRequest> prepareActionRequests(Action action) {
    final ActionRequestContext context =
        decoratorContextFactory.getActionRequestDecoratorContext(action);

    // If action is sampleUnitType B and handler type NOTIFY
    // then create an action request per respondent
    log.with("actionId", action.getId()).trace("Setting action request array for processing");
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

  /** Deal with a single action cancel - the transaction boundary is here */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processActionCancel(final Action action) {

    log.with("action_id", action.getId())
        .with("case_id", action.getCaseId())
        .with("action_plan_pk", action.getActionPlanFK())
        .info("processing action cancel");

    try {
      actionStateService.transitionAction(action, ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED);
    } catch (CTPException ctpExeption) {
      throw new IllegalStateException(ctpExeption);
    }
  }

  private void cancelAction(final Action action) {
    log.with("action_id", action.getId())
        .with("case_id", action.getCaseId())
        .with("action_plan_pk", action.getActionPlanFK())
        .info("cancelling action");
    try {
      actionStateService.transitionAction(action, ActionDTO.ActionEvent.REQUEST_CANCELLED);
    } catch (CTPException ctpExeption) {
      throw new IllegalStateException(ctpExeption);
    }
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

    ActionDTO.ActionEvent event = getActionEvent(actionType);

    try {
      actionStateService.transitionAction(action, event);
    } catch (CTPException ctpExeption) {
      throw new IllegalStateException(ctpExeption);
    }
  }

  private ActionDTO.ActionEvent getActionEvent(ActionType actionType) {
    Boolean responseRequired = actionType.getResponseRequired();
    if (responseRequired == null) {
      responseRequired = Boolean.FALSE;
    }
    ActionDTO.ActionEvent event =
        responseRequired
            ? ActionDTO.ActionEvent.REQUEST_DISTRIBUTED
            : ActionDTO.ActionEvent.REQUEST_COMPLETED;
    return event;
  }

  /**
   * To validate an ActionType
   *
   * @param action the action to validate
   * @return true if valid
   */
  private boolean valid(final Action action) {
    final ActionType actionType = action.getActionType();
    boolean valid = actionType.getResponseRequired() != null;
    if (!valid) {
      log.error("ActionType is not valid for action");
    }
    return valid;
  }
}
