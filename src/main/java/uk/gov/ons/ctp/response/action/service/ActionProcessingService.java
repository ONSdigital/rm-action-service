package uk.gov.ons.ctp.response.action.service;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
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
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

@Slf4j
public abstract class ActionProcessingService {
  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  public static final String CANCELLATION_REASON = "Action cancelled by Response Management";
  public static final String ENABLED = "ENABLED";
  public static final String PENDING = "PENDING";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";

  @Autowired private CaseSvcClientService caseSvcClientService;

  @Autowired private ActionRepository actionRepo;

  @Autowired private ActionInstructionPublisher actionInstructionPublisher;

  @Autowired
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  private ActionRequestDecorator[] decorators;

  @Autowired private ActionRequestValidator validator;

  public abstract ActionRequestContextFactory getActionRequestDecoratorContextFactory();

  public ActionProcessingService(ActionRequestDecorator[] decorators) {
    this.decorators = decorators;
  }

  public ActionRequest prepareActionRequest(Action action) {
    final ActionRequestContextFactory factory = getActionRequestDecoratorContextFactory();
    final ActionRequestContext context = factory.getActionRequestDecoratorContext(action);
    final ActionRequest actionRequest = new ActionRequest();

    Arrays.stream(this.decorators).forEach(d -> d.decorateActionRequest(actionRequest, context));

    return actionRequest;
  }

  /**
   * Deal with a single action - the transaction boundary is here.
   *
   * <p>The processing requires numerous calls to Case service, to write to our own action table and
   * to publish to queue.
   *
   * @param action the action to deal with
   */
  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      rollbackFor = Exception.class)
  public void processActionRequest(final Action action) throws CTPException {
    log.debug(
        "processing actionRequest with actionid {} caseid {} actionplanFK {}",
        action.getId(),
        action.getCaseId(),
        action.getActionPlanFK());

    final ActionType actionType = action.getActionType();
    if (valid(actionType)) {
      final ActionDTO.ActionEvent event =
          actionType.getResponseRequired()
              ? ActionDTO.ActionEvent.REQUEST_DISTRIBUTED
              : ActionDTO.ActionEvent.REQUEST_COMPLETED;

      transitionAction(action, event);

      final ActionRequest actionRequest = prepareActionRequest(action);

      if (actionRequest != null) {
        actionInstructionPublisher.sendActionInstruction(actionType.getHandler(), actionRequest);
      }

      // advise casesvc to create a corresponding caseevent for our action
      caseSvcClientService.createNewCaseEvent(action, CategoryDTO.CategoryName.ACTION_CREATED);
    } else {
      log.error(
          "Unexpected situation. actionType is not defined for action with actionid {}",
          action.getId());
    }
  }

  /**
   * Deal with a single action cancel - the transaction boundary is here
   *
   * @param action the action to deal with
   */
  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      rollbackFor = Exception.class)
  public void processActionCancel(final Action action) throws CTPException {
    log.info(
        "processing action cancel for actionid {} caseid {} actionplanFK {}",
        action.getId(),
        action.getCaseId(),
        action.getActionPlanFK());

    transitionAction(action, ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED);

    actionInstructionPublisher.sendActionInstruction(
        action.getActionType().getHandler(), prepareActionCancel(action));

    // advise casesvc to create a corresponding caseevent for our action
    caseSvcClientService.createNewCaseEvent(
        action, CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED);
  }

  /**
   * Take an action and using it, fetch further info from Case service in a number of rest calls, in
   * order to create the ActionRequest
   *
   * @param action It all starts wih the Action
   * @return The ActionRequest created from the Action and the other info from CaseSvc
   */
  private ActionCancel prepareActionCancel(final Action action) {
    log.debug(
        "Building ActionCancel to publish to downstream handler for action id {} and case id {}",
        action.getActionPK(),
        action.getCaseId());
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
  private void transitionAction(final Action action, final ActionDTO.ActionEvent event)
      throws CTPException {
    final ActionDTO.ActionState nextState =
        actionSvcStateTransitionManager.transition(action.getState(), event);
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
