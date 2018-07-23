package uk.gov.ons.ctp.response.action.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.util.HashMap;
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
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanPostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanPutRequestDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

@Api("API for action plans")
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
@Slf4j
public class ActionPlanEndpoint implements CTPEndpoint {

  static final String ACTION_PLAN_NOT_FOUND = "ActionPlan not found for id %s";

  private ActionPlanService actionPlanService;

  private MapperFacade mapperFacade;

  @Autowired
  public ActionPlanEndpoint(
      final ActionPlanService actionPlanService,
      final @Qualifier("actionBeanMapper") MapperFacade mapperFacade) {
    this.actionPlanService = actionPlanService;
    this.mapperFacade = mapperFacade;
  }

  @ApiOperation(value = "List action plans for the optional selectors, most recent first")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action plans for the optional selectors"),
    @ApiResponse(code = 204, message = "Action plans not found"),
  })
  @RequestMapping(method = RequestMethod.GET)
  public final ResponseEntity<List<ActionPlanDTO>> findActionPlans(
      final @RequestParam HashMap<String, String> selectors) {
    log.info("Retrieving action plans, Selectors: {}", selectors);

    final List<ActionPlan> actionPlans;
    if (!selectors.isEmpty()) {
      actionPlans = actionPlanService.findActionPlansBySelectors(selectors);
    } else {
      actionPlans = actionPlanService.findActionPlans();
    }

    log.info("Successfully retrieved action plans, Selectors={}", selectors);
    final List<ActionPlanDTO> actionPlanDTOs =
        mapperFacade.mapAsList(actionPlans, ActionPlanDTO.class);
    return CollectionUtils.isEmpty(actionPlanDTOs)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(actionPlanDTOs);
  }

  @ApiOperation(value = "Get the action plan for an actionPlanId")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action plan has been updated"),
    @ApiResponse(code = 404, message = "Action plan not found"),
  })
  @RequestMapping(value = "/{actionplanid}", method = RequestMethod.GET)
  public final ActionPlanDTO findActionPlanByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId) throws CTPException {
    log.info("Entering findActionPlanByActionPlanId with {}", actionPlanId);
    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }
    return mapperFacade.map(actionPlan, ActionPlanDTO.class);
  }

  @ApiOperation(value = "Create an action plan")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 201, message = "Action plan has been created"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
    @ApiResponse(code = 409, message = "Action plan already exists"),
  })
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public final ResponseEntity<ActionPlanDTO> createActionPlan(
      @RequestBody @Valid final ActionPlanPostRequestDTO request, final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.info(
        "Creating action plan, Name: {}, Selectors: {}", request.getName(), request.getSelectors());

    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for create action plan: ", bindingResult);
    }

    // Check if action plan with same name already exists
    ActionPlan existingActionPlan = actionPlanService.findActionPlanByName(request.getName());
    if (existingActionPlan != null) {
      final String message = "Action plan with name " + request.getName() + " already exists";
      throw new CTPException(CTPException.Fault.RESOURCE_VERSION_CONFLICT, message);
    }

    ActionPlan actionPlan = mapperFacade.map(request, ActionPlan.class);
    ActionPlan createdActionPlan = actionPlanService.createActionPlan(actionPlan);

    ActionPlanDTO actionPlanDTO = mapperFacade.map(createdActionPlan, ActionPlanDTO.class);
    final String newResourceUrl =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(actionPlanDTO.getId())
            .toUri()
            .toString();
    return ResponseEntity.created(URI.create(newResourceUrl)).body(actionPlanDTO);
  }

  @ApiOperation(value = "Update action plan")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 200, message = "Action plan has been updated"),
    @ApiResponse(code = 404, message = "Action plan not found"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
  })
  @RequestMapping(
      value = "/{actionplanid}",
      method = RequestMethod.PUT,
      consumes = "application/json")
  public final ActionPlanDTO updateActionPlanByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId,
      @RequestBody(required = false) @Valid final ActionPlanPutRequestDTO request,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.info(
        "UpdateActionPlanByActionPlanId with actionplanid {} - actionPlan {}",
        actionPlanId,
        request);
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for update action plan: ", bindingResult);
    }

    ActionPlan actionPlan = mapperFacade.map(request, ActionPlan.class);

    final ActionPlan updatedActionPlan =
        actionPlanService.updateActionPlan(actionPlanId, actionPlan);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }
    return mapperFacade.map(updatedActionPlan, ActionPlanDTO.class);
  }
}
