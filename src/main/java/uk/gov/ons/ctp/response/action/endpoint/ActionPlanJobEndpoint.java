package uk.gov.ons.ctp.response.action.endpoint;

import static uk.gov.ons.ctp.response.action.endpoint.ActionPlanEndpoint.ACTION_PLAN_NOT_FOUND;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobRequestDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

/** The REST endpoint controller for ActionPlanJobs. */
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
@Slf4j
public class ActionPlanJobEndpoint implements CTPEndpoint {

  static final String ACTION_PLAN_JOB_NOT_FOUND = "ActionPlanJob not found for id %s";

  @Autowired private ActionPlanJobService actionPlanJobService;

  @Autowired private ActionPlanService actionPlanService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  @ApiOperation(value = "Get the Action plan job for an actionPlanJobId")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action plan job for the actionPlanJobId"),
    @ApiResponse(code = 404, message = "Action plan job not found"),
  })
  @RequestMapping(value = "/jobs/{actionplanjobid}", method = RequestMethod.GET)
  public final ActionPlanJobDTO findActionPlanJobById(
      @PathVariable("actionplanjobid") final UUID actionPlanJobId) throws CTPException {
    log.info("Entering findActionPlanJobById with {}", actionPlanJobId);
    final ActionPlanJob actionPlanJob = actionPlanJobService.findActionPlanJob(actionPlanJobId);

    if (actionPlanJob == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_JOB_NOT_FOUND, actionPlanJobId);
    }

    final ActionPlan actionPlan = actionPlanService.findActionPlan(actionPlanJob.getActionPlanFK());
    final ActionPlanJobDTO actionPlanJobDTO =
        mapperFacade.map(actionPlanJob, ActionPlanJobDTO.class);
    actionPlanJobDTO.setActionPlanId(actionPlan.getId());
    return actionPlanJobDTO;
  }

  @ApiOperation(value = "List Action plan job for an actionPlanId, most recent first")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action plan jobs for the actionPlanId"),
    @ApiResponse(code = 204, message = "Action plan jobs not found"),
  })
  @RequestMapping(value = "/{actionplanid}/jobs", method = RequestMethod.GET)
  public final ResponseEntity<List<ActionPlanJobDTO>> findAllActionPlanJobsByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId) throws CTPException {
    log.info("Entering findAllActionPlanJobsByActionPlanId with {}", actionPlanId);
    final List<ActionPlanJob> actionPlanJobs =
        actionPlanJobService.findActionPlanJobsForActionPlan(actionPlanId);
    if (CollectionUtils.isEmpty(actionPlanJobs)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionPlanJobDTOs(actionPlanJobs, actionPlanId));
    }
  }

  @ApiOperation(value = "Create an action plan job (i.e. execute the action plan)")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 201, message = "Action plan job has been created"),
    @ApiResponse(code = 404, message = "Action plan not found"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
  })
  @RequestMapping(
      value = "/{actionplanid}/jobs",
      method = RequestMethod.POST,
      consumes = "application/json")
  public final ResponseEntity<ActionPlanJobDTO> executeActionPlan(
      @PathVariable("actionplanid") final UUID actionPlanId,
      final @RequestBody @Valid ActionPlanJobRequestDTO actionPlanJobRequestDTO,
      final BindingResult binding)
      throws CTPException, InvalidRequestException {
    log.info("Entering executeActionPlan with {}", actionPlanId);

    if (binding.hasErrors()) {
      throw new InvalidRequestException("Binding errors for execute action plan: ", binding);
    }

    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }
    ActionPlanJob job = mapperFacade.map(actionPlanJobRequestDTO, ActionPlanJob.class);
    job.setActionPlanFK(actionPlan.getActionPlanPK());
    job = actionPlanJobService.createAndExecuteActionPlanJob(job);
    if (job == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }
    final ActionPlanJobDTO result = mapperFacade.map(job, ActionPlanJobDTO.class);
    result.setActionPlanId(actionPlanId);

    final String newResourceUrl =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(result.getId())
            .toUri()
            .toString();

    return ResponseEntity.created(URI.create(newResourceUrl)).body(result);
  }

  /**
   * To build a list of ActionPlanJobDTOs from ActionPlanJobs populating the actionPlanUUID
   *
   * @param actionPlanJobs a list of ActionPlanJobs
   * @param actionPlanId Id of ActionPlan
   * @return a list of ActionPlanJobDTOs
   */
  private List<ActionPlanJobDTO> buildActionPlanJobDTOs(
      final List<ActionPlanJob> actionPlanJobs, final UUID actionPlanId) {
    final List<ActionPlanJobDTO> actionPlanJobsDTOs =
        mapperFacade.mapAsList(actionPlanJobs, ActionPlanJobDTO.class);

    for (final ActionPlanJobDTO actionPlanJobDTO : actionPlanJobsDTOs) {
      actionPlanJobDTO.setActionPlanId(actionPlanId);
    }

    return actionPlanJobsDTOs;
  }
}
