package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.decorator.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO;

public abstract class ActionProcessingService {
  private static final Logger log = LoggerFactory.getLogger(ActionProcessingService.class);

  public static final String ACTION_TYPE_NOT_DEFINED = "ActionType is not defined for action";
  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  public static final String DATE_FORMAT_IN_SOCIAL_LETTER = "dd/MM";
  public static final String CANCELLATION_REASON = "Action cancelled by Response Management";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";
  public static final String ENABLED = "ENABLED";
  public static final String NOTIFY = "Notify";
  public static final String PENDING = "PENDING";

  @Autowired private ActionRepository actionRepo;

  @Autowired private ActionInstructionPublisher actionInstructionPublisher;

  @Autowired private NotifyService notifyService;

  @Autowired
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  private ActionRequestDecorator[] decorators;

  public abstract ActionRequestContextFactory getActionRequestDecoratorContextFactory();

  public ActionProcessingService(ActionRequestDecorator[] decorators) {
    this.decorators = decorators;
  }

  /** Distributes requests for a single action */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processActionRequests(final UUID actionId) {
    Action action = actionRepo.findById(actionId);
    log.with("action_id", action.getId()).debug("Processing actionRequest");

    final ActionType actionType = action.getActionType();
    if (!valid(actionType)) {
      log.with("action", action).error("ActionType is not defined for action");
      throw new IllegalStateException(ACTION_TYPE_NOT_DEFINED);
    }

    List<ActionRequest> actionRequests = prepareActionRequests(action);

    ActionDTO.ActionEvent event =
        actionType.getResponseRequired()
            ? ActionDTO.ActionEvent.REQUEST_DISTRIBUTED
            : ActionDTO.ActionEvent.REQUEST_COMPLETED;
    transitionAction(action, event);

    actionRequests.forEach(
        actionRequest -> {
          if (actionRequest.isPubsub()) {
            log.info("Pubsub message will be forwarded to notify cloudfunction");
            notifyService.processNotification(actionRequest);
          } else {
            actionInstructionPublisher.sendActionInstruction(
                actionType.getHandler(), actionRequest);
          }
        });
  }

  private List<ActionRequest> prepareActionRequests(Action action) {
    final ActionRequestContextFactory factory = getActionRequestDecoratorContextFactory();
    final ActionRequestContext context = factory.getActionRequestDecoratorContext(action);

    // If action is sampleUnitType B and handler type NOTIFY
    // then create an action request per respondent
    ArrayList<ActionRequest> actionRequests = new ArrayList<>();
    if (isBusinessNotification(context)) {
      context
          .getChildParties()
          .forEach(
              p -> {
                log.info("Creating Action request for pubsub notify");
                context.setChildParties(Collections.singletonList(p));
                ActionRequest actionRequest = prepareActionRequest(context);
                actionRequest.setIsPubsub(true);
                actionRequests.add(actionRequest);
                log.with("isPubsub", actionRequest.isPubsub()).info("Pubsub notify action added to the list")
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
    Arrays.stream(this.decorators).forEach(d -> d.decorateActionRequest(actionRequest, context));
    return actionRequest;
  }

  /** Deal with a single action cancel - the transaction boundary is here */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processActionCancel(final UUID actionId) {
    Action action = actionRepo.findById(actionId);

    log.with("action_id", action.getId())
        .with("case_id", action.getCaseId())
        .with("action_plan_pk", action.getActionPlanFK())
        .info("processing action cancel");

    transitionAction(action, ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED);

    actionInstructionPublisher.sendActionInstruction(
        action.getActionType().getHandler(), prepareActionCancel(action));
  }

  /**
   * Take an action and using it, fetch further info from Case service in a number of rest calls, in
   * order to create the ActionRequest
   *
   * @param action It all starts wih the Action
   * @return The ActionRequest created from the Action and the other info from CaseSvc
   */
  private ActionCancel prepareActionCancel(final Action action) {
    log.with("action_id", action.getId())
        .with("case_id", action.getCaseId())
        .with("action_plan_pk", action.getActionPlanFK())
        .debug("Building ActionCancel to publish to downstream handler");
    final ActionCancel actionCancel = new ActionCancel();
    actionCancel.setActionId(action.getId().toString());
    actionCancel.setResponseRequired(true);
    actionCancel.setReason(CANCELLATION_REASON);
    return actionCancel;
  }

  /**
   * Change the action status in db to indicate we have sent this action downstream, and clear
   * previous situation (in the scenario where the action has prev. failed)
   *
   * @param action the action to change and persist
   * @param event the event to transition the action with
   * @throws CTPException if action state transition error
   */
  private void transitionAction(final Action action, final ActionDTO.ActionEvent event) {
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

  /**
   * To validate an ActionType
   *
   * @param actionType the ActionType to validate
   * @return true if valid
   */
  private boolean valid(final ActionType actionType) {
    return (actionType != null) && (actionType.getResponseRequired() != null);
  }
}
