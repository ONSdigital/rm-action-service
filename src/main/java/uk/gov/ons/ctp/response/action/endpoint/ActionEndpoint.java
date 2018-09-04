package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionFeedbackRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPutRequestDTO;
import uk.gov.ons.ctp.response.action.service.ActionCaseService;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.service.ActionService;

/** The REST endpoint controller for Actions. */
@RestController
@RequestMapping(value = "/actions", produces = "application/json")
public final class ActionEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionEndpoint.class);

  public static final String ACTION_NOT_FOUND = "Action not found for id %s";
  public static final String ACTION_NOT_UPDATED = "Action not updated for id %s";
  public static final String CASE_NOT_FOUND = "Case not found for id %s";
  @Autowired private ActionService actionService;
  @Autowired private ActionPlanService actionPlanService;
  @Autowired private ActionCaseService actionCaseService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * GET the Action for the specified action id.
   *
   * @param actionId Action Id of requested Action
   * @return ActionDTO Returns the associated Action for the specified action
   * @throws CTPException if no associated Action found for the specified action Id.
   */
  @RequestMapping(value = "/{actionid}", method = RequestMethod.GET)
  public ActionDTO findActionByActionId(@PathVariable("actionid") final UUID actionId)
      throws CTPException {
    log.with("action_id", actionId).debug("Entering findActionByActionId");
    final Action action = actionService.findActionById(actionId);
    if (action == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_NOT_FOUND, actionId);
    }

    final ActionDTO actionDTO = mapperFacade.map(action, ActionDTO.class);
    final UUID actionPlanUUID = actionPlanService.findActionPlan(action.getActionPlanFK()).getId();
    actionDTO.setActionPlanId(actionPlanUUID);
    return actionDTO;
  }

  /**
   * GET Actions for the specified case Id.
   *
   * @param caseId caseID to which Actions apply
   * @return List<ActionDTO> Returns the associated actions for the specified case id.
   */
  @RequestMapping(value = "/case/{caseid}", method = RequestMethod.GET)
  public ResponseEntity<List<ActionDTO>> findActionsByCaseId(
      @PathVariable("caseid") final UUID caseId) {
    log.debug("Entering findActionsByCaseId...");
    final List<Action> actions = actionService.findActionsByCaseId(caseId);
    if (CollectionUtils.isEmpty(actions)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionsDTOs(actions));
    }
  }

  /**
   * GET all Actions optionally filtered by ActionType and or state
   *
   * @param actionType Optional filter by ActionType
   * @param state Optional filter by Action state
   * @return List<ActionDTO> Actions for the specified filters
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ActionDTO>> findActions(
      @RequestParam(value = "actiontype", required = false) final String actionType,
      @RequestParam(value = "state", required = false) final ActionDTO.ActionState state) {
    List<Action> actions = null;

    if (actionType != null) {
      if (state != null) {
        log.with("action_type", actionType)
            .with("state", state)
            .debug("Entering findActionsByTypeAndState");
        actions =
            actionService.findActionsByTypeAndStateOrderedByCreatedDateTimeDescending(
                actionType, state);
      } else {
        log.with("action_type", actionType).debug("Entering findActionsByType");
        actions = actionService.findActionsByType(actionType);
      }
    } else {
      if (state != null) {
        log.with("state", state).debug("Entering findActionsByState");
        actions = actionService.findActionsByState(state);
      } else {
        log.debug("Entering findAllActionsOrderedByCreatedDateTimeDescending");
        actions = actionService.findAllActionsOrderedByCreatedDateTimeDescending();
      }
    }

    if (CollectionUtils.isEmpty(actions)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionsDTOs(actions));
    }
  }

  /**
   * POST Create an adhoc Action.
   *
   * @param actionPostRequestDTO Incoming ActionDTO with details to validate and from which to
   *     create Action
   * @param bindingResult collects errors thrown by update
   * @return ActionDTO Created Action
   * @throws CTPException on failure to create Action
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ActionDTO> createAdhocAction(
      final @RequestBody @Valid ActionPostRequestDTO actionPostRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("action_post_request", actionPostRequestDTO).debug("Entering createAdhocAction");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for create action: ", bindingResult);
    }

    final UUID parentCaseId = actionPostRequestDTO.getCaseId();
    final ActionCase parentCase = actionCaseService.findActionCase(parentCaseId);
    if (parentCase != null) {
      ActionPlan actionPlan =
          this.actionPlanService.findActionPlanById(parentCase.getActionPlanId());
      Action action = mapperFacade.map(actionPostRequestDTO, Action.class);
      action.setCaseFK(parentCase.getCasePK());
      // Action plans are optional for ad-hoc actions
      if (actionPlan != null) {
        action.setActionPlanFK(actionPlan.getActionPlanPK());
      }
      action = actionService.createAction(action);

      final ActionDTO actionDTO = mapperFacade.map(action, ActionDTO.class);
      final String newResourceUrl =
          ServletUriComponentsBuilder.fromCurrentRequest()
              .buildAndExpand(actionDTO.getId())
              .toUri()
              .toString();
      return ResponseEntity.created(URI.create(newResourceUrl)).body(actionDTO);
    } else {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, CASE_NOT_FOUND, parentCaseId);
    }
  }

  /**
   * PUT to update the specified Action.
   *
   * @param actionId Action Id of the Action to update
   * @param actionPutRequestDTO Incoming ActionDTO with details to update
   * @param bindingResult collects errors thrown by update
   * @return ActionDTO Returns the updated Action details
   * @throws CTPException if update operation fails
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(value = "/{actionid}", method = RequestMethod.PUT, consumes = "application/json")
  public ActionDTO updateAction(
      @PathVariable("actionid") final UUID actionId,
      @RequestBody(required = false) @Valid final ActionPutRequestDTO actionPutRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("action_id", actionId)
        .with("action_put_request", actionPutRequestDTO)
        .debug("Updating Action");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for update action: ", bindingResult);
    }

    Action actionToUpdate = mapperFacade.map(actionPutRequestDTO, Action.class);
    actionToUpdate.setId(actionId);
    actionToUpdate = actionService.updateAction(actionToUpdate);
    if (actionToUpdate == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_NOT_UPDATED, actionId);
    }

    final ActionDTO resultDTO = mapperFacade.map(actionToUpdate, ActionDTO.class);
    final UUID actionPlanUUID =
        actionPlanService.findActionPlan(actionToUpdate.getActionPlanFK()).getId();
    resultDTO.setActionPlanId(actionPlanUUID);
    return resultDTO;
  }

  /**
   * PUT to cancel all the Actions for a specified caseId.
   *
   * @param caseId Case Id of the actions to cancel
   * @return List<ActionDTO> Returns a list of cancelled Actions
   * @throws CTPException if update operation fails
   */
  @RequestMapping(
      value = "/case/{caseid}/cancel",
      method = RequestMethod.PUT,
      consumes = "application/json")
  public ResponseEntity<List<ActionDTO>> cancelActions(@PathVariable("caseid") final UUID caseId)
      throws CTPException {
    log.with("case_id", caseId).info("Cancelling Actions");

    final ActionCase caze = actionCaseService.findActionCase(caseId);
    if (caze == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, CASE_NOT_FOUND, caseId);
    }

    final List<Action> actions = actionService.cancelActions(caseId);
    if (CollectionUtils.isEmpty(actions)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionsDTOs(actions));
    }
  }

  /**
   * Allow feedback otherwise sent via JMS to be sent via endpoint
   *
   * @param actionId the action
   * @param actionFeedbackRequestDTO the feedback
   * @param bindingResult the bindingResult
   * @return the modified action
   * @throws CTPException oops
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(
      value = "/{actionid}/feedback",
      method = RequestMethod.PUT,
      consumes = {"application/json"})
  public ActionDTO feedbackAction(
      @PathVariable("actionid") final UUID actionId,
      @RequestBody @Valid final ActionFeedbackRequestDTO actionFeedbackRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("action_id", actionId)
        .with("action_feedback_request", actionFeedbackRequestDTO)
        .debug("Feedback for Action");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for feedback action: ", bindingResult);
    }

    final ActionFeedback actionFeedback =
        mapperFacade.map(actionFeedbackRequestDTO, ActionFeedback.class);
    actionFeedback.setActionId(actionId.toString());
    final Action action = actionService.feedBackAction(actionFeedback);
    if (action == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_NOT_FOUND, actionId);
    }

    final ActionDTO resultDTO = mapperFacade.map(action, ActionDTO.class);
    final UUID actionPlanUUID = actionPlanService.findActionPlan(action.getActionPlanFK()).getId();
    resultDTO.setActionPlanId(actionPlanUUID);
    return resultDTO;
  }

  /**
   * To build a list of ActionDTOs from Actions populating the actionPlanUUID
   *
   * @param actions a list of Actions
   * @return a list of ActionDTOs
   */
  private List<ActionDTO> buildActionsDTOs(final List<Action> actions) {
    final List<ActionDTO> actionsDTOs = mapperFacade.mapAsList(actions, ActionDTO.class);

    int index = 0;
    for (final Action action : actions) {
      final int actionPlanFK = action.getActionPlanFK();
      final UUID actionPlanUUID = actionPlanService.findActionPlan(actionPlanFK).getId();
      actionsDTOs.get(index).setActionPlanId(actionPlanUUID);
      index = index + 1;
    }

    return actionsDTOs;
  }
}
