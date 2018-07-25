package uk.gov.ons.ctp.response.action.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Api("API for actions")
@RestController
@RequestMapping(value = "/actions", produces = "application/json")
@Slf4j
public final class ActionEndpoint implements CTPEndpoint {

  static final String ACTION_NOT_FOUND = "Action not found for id %s";
  static final String ACTION_NOT_UPDATED = "Action not updated for id %s";
  static final String CASE_NOT_FOUND = "Case not found for id %s";

  @Autowired private ActionService actionService;
  @Autowired private ActionPlanService actionPlanService;
  @Autowired private ActionCaseService actionCaseService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  @ApiOperation("Get the action for an actionId")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action for the actionId"),
    @ApiResponse(code = 404, message = "Action not found"),
  })
  @RequestMapping(value = "/{actionid}", method = RequestMethod.GET)
  public ActionDTO findActionByActionId(@PathVariable("actionid") final UUID actionId)
      throws CTPException {
    log.info("Entering findActionByActionId with {}", actionId);
    final Action action = actionService.findActionById(actionId);
    if (action == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_NOT_FOUND, actionId);
    }

    final ActionDTO actionDTO = mapperFacade.map(action, ActionDTO.class);
    final UUID actionPlanUUID = actionPlanService.findActionPlan(action.getActionPlanFK()).getId();
    actionDTO.setActionPlanId(actionPlanUUID);
    return actionDTO;
  }

  @ApiOperation("List actions for a case")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Actions for the case"),
    @ApiResponse(code = 204, message = "Action not found"),
  })
  @RequestMapping(value = "/case/{caseid}", method = RequestMethod.GET)
  public ResponseEntity<List<ActionDTO>> findActionsByCaseId(
      @PathVariable("caseid") final UUID caseId) {
    log.info("Entering findActionsByCaseId..");
    final List<Action> actions = actionService.findActionsByCaseId(caseId);
    if (CollectionUtils.isEmpty(actions)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionsDTOs(actions));
    }
  }

  @ApiOperation(value = "List actions with the actionType and state, most recent first")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Actions for the actionType and state"),
    @ApiResponse(code = 204, message = "Action not found"),
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ActionDTO>> findActions(
      @RequestParam(value = "actiontype", required = false) final String actionType,
      @RequestParam(value = "state", required = false) final ActionDTO.ActionState state) {
    List<Action> actions = null;

    if (actionType != null) {
      if (state != null) {
        log.info("Entering findActionsByTypeAndState with {} {}", actionType, state);
        actions =
            actionService.findActionsByTypeAndStateOrderedByCreatedDateTimeDescending(
                actionType, state);
      } else {
        log.info("Entering findActionsByType with {}", actionType);
        actions = actionService.findActionsByType(actionType);
      }
    } else {
      if (state != null) {
        log.info("Entering findActionsByState with {}", state);
        actions = actionService.findActionsByState(state);
      } else {
        log.info("Entering findAllActionsOrderedByCreatedDateTimeDescending");
        actions = actionService.findAllActionsOrderedByCreatedDateTimeDescending();
      }
    }

    if (CollectionUtils.isEmpty(actions)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionsDTOs(actions));
    }
  }

  @ApiOperation(value = "Create an Action")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 201, message = "Action has been created"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
  })
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ActionDTO> createAdhocAction(
      final @RequestBody @Valid ActionPostRequestDTO actionPostRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.debug("Entering createAdhocAction with actionPostRequestDTO {}", actionPostRequestDTO);
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

  @ApiOperation(value = "Update an Action")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action has been updated"),
    @ApiResponse(code = 404, message = "Action not found"),
  })
  @RequestMapping(value = "/{actionid}", method = RequestMethod.PUT, consumes = "application/json")
  public ActionDTO updateAction(
      @PathVariable("actionid") final UUID actionId,
      @RequestBody(required = false) @Valid final ActionPutRequestDTO actionPutRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.info("Updating Action with {} - {}", actionId, actionPutRequestDTO);
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

  @ApiOperation(value = "Cancel all the actions for a case")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 200, message = "Cancelled action"),
    @ApiResponse(code = 204, message = "No actions to be cancelled"),
    @ApiResponse(code = 404, message = "Case not found"),
  })
  @RequestMapping(
      value = "/case/{caseid}/cancel",
      method = RequestMethod.PUT,
      consumes = "application/json")
  public ResponseEntity<List<ActionDTO>> cancelActions(@PathVariable("caseid") final UUID caseId)
      throws CTPException {
    log.info("Cancelling Actions for {}", caseId);

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

  @ApiOperation(value = "Update state of the action")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 200, message = "Action has been updated"),
    @ApiResponse(code = 404, message = "Action not found"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
  })
  @RequestMapping(
      value = "/{actionid}/feedback",
      method = RequestMethod.PUT,
      consumes = {"application/json"})
  public ActionDTO feedbackAction(
      @PathVariable("actionid") final UUID actionId,
      @RequestBody @Valid final ActionFeedbackRequestDTO actionFeedbackRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.info("Feedback for Action {} - {}", actionId, actionFeedbackRequestDTO);
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
