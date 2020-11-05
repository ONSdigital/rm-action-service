package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import java.util.HashMap;
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
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanPostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanPutRequestDTO;
import uk.gov.ons.ctp.response.action.scheduled.plan.ActionPlanJobExecutor;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.error.InvalidRequestException;

/** The REST endpoint controller for ActionPlans. */
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
public class ActionPlanEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanEndpoint.class);

  public static final String ACTION_PLAN_NOT_FOUND = "ActionPlan not found for id %s";

  private ActionPlanService actionPlanService;
  private final ActionPlanJobExecutor actionPlanJobExecutor;

  private MapperFacade mapperFacade;

  @Autowired
  public ActionPlanEndpoint(
      final ActionPlanService actionPlanService,
      ActionPlanJobExecutor actionPlanJobExecutor,
      final @Qualifier("actionBeanMapper") MapperFacade mapperFacade) {
    this.actionPlanService = actionPlanService;
    this.actionPlanJobExecutor = actionPlanJobExecutor;
    this.mapperFacade = mapperFacade;
  }

  /**
   * This method returns all action plans.
   *
   * @return List<ActionPlanDTO> This returns all action plans.
   */
  @RequestMapping(method = RequestMethod.GET)
  public final ResponseEntity<List<ActionPlanDTO>> findActionPlans(
      final @RequestParam HashMap<String, String> selectors) {
    log.with(selectors).debug("Retrieving action plans with selectors");

    final List<ActionPlan> actionPlans;
    if (!selectors.isEmpty()) {
      actionPlans = actionPlanService.findActionPlansBySelectors(selectors);
    } else {
      actionPlans = actionPlanService.findActionPlans();
    }

    log.with(selectors).debug("Successfully retrieved action plans with selectors");
    final List<ActionPlanDTO> actionPlanDTOs =
        mapperFacade.mapAsList(actionPlans, ActionPlanDTO.class);
    return CollectionUtils.isEmpty(actionPlanDTOs)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(actionPlanDTOs);
  }

  /**
   * This method returns the associated action plan after it has been created.
   *
   * @param request The object created by ActionPlanPostRequestDTO from the json found in the
   *     request body
   * @param bindingResult collects errors thrown by create
   * @return ActionPlanDTO This returns the updated action plan.
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public final ResponseEntity<ActionPlanDTO> createActionPlan(
      @RequestBody @Valid final ActionPlanPostRequestDTO request, final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("name", request.getName())
        .with("selectors", request.getSelectors())
        .debug("Creating action plan");

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

  /**
   * This method returns the associated action plan after it has been updated. Note that only the
   * description and the lastGoodRunDatetime can be updated.
   *
   * @param actionPlanId This is the action plan id
   * @param request The object created by ActionPlanDTOMessageBodyReader from the json found in the
   *     request body
   * @param bindingResult collects errors thrown by update
   * @return ActionPlanDTO This returns the updated action plan.
   * @throws CTPException if the json provided is incorrect or if the action plan id does not exist.
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(
      value = "/{actionplanid}",
      method = RequestMethod.PUT,
      consumes = "application/json")
  public final ActionPlanDTO updateActionPlanByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId,
      @RequestBody(required = false) @Valid final ActionPlanPutRequestDTO request,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("action_plan_id", actionPlanId)
        .debug("UpdateActionPlanByActionPlanId with actionplanid");
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

  /**
   * This method creates and executes all action plan jobs.
   *
   * <p>Note: This can only be run by one instance of action at a time. If multiple instances have
   * this invoked at the same time, duplicate actions will be created.
   *
   * @throws CTPException if no action plan job found for the specified action plan job id.
   */
  @RequestMapping(value = "/execute", method = RequestMethod.GET)
  public final ResponseEntity<String> createAndExecuteAllActionPlanJobs() throws CTPException {
    try {
      log.info("About to begin creating and executing action plan jobs");
      actionPlanJobExecutor.createAndExecuteAllActionPlanJobs();
      log.info("Completed creating and executing action plan jobs");
      return ResponseEntity.ok().body("Completed creating and executing action plan jobs");
    } catch (RuntimeException e) {
      log.error(
          "Uncaught exception - transaction rolled back. Will re-run when scheduled by cron", e);
      throw new CTPException(
          CTPException.Fault.SYSTEM_ERROR,
          "Uncaught exception when creating and execution action plan jobs");
    }
  }
}
