package uk.gov.ons.ctp.response.action.endpoint;

import static uk.gov.ons.ctp.response.action.endpoint.ActionPlanEndpoint.ACTION_PLAN_NOT_FOUND;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
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
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

/** The REST endpoint controller for ActionPlanJobs. */
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
public class ActionPlanJobEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobEndpoint.class);

  public static final String ACTION_PLAN_JOB_NOT_FOUND = "ActionPlanJob not found for id %s";

  private ActionPlanJobService actionPlanJobService;
  private ActionPlanService actionPlanService;

  private MapperFacade mapperFacade;

  public ActionPlanJobEndpoint(ActionPlanJobService actionPlanJobService,
                               ActionPlanService actionPlanService,
                               @Qualifier("actionBeanMapper")
                                 MapperFacade mapperFacade) {
    this.actionPlanJobService = actionPlanJobService;
    this.actionPlanService = actionPlanService;
    this.mapperFacade = mapperFacade;
  }

  /**
   * This method returns the associated action plan job for the specified action plan job id.
   *
   * @param actionPlanJobId This is the action plan job id
   * @return ActionPlanJobDTO This returns the associated action plan job for the specified action
   *     plan job id.
   * @throws CTPException if no action plan job found for the specified action plan job id.
   */
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

  /**
   * Returns all action plan jobs for the given action plan id.
   *
   * @param actionPlanId the given action plan id.
   * @return Returns all action plan jobs for the given action plan id.
   * @throws CTPException summats went wrong
   */
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

  /**
   * To create a new Action Plan Job having received an action plan id and some json
   *
   * @param actionPlanId the given action plan id.
   * @param binding collects errors thrown by update
   * @return the created ActionPlanJobDTO
   * @throws CTPException summats went wrong
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(
      value = "/{actionplanid}/jobs",
      method = RequestMethod.POST,
      consumes = "application/json")
  public final ResponseEntity<ActionPlanJobDTO> executeActionPlan(
      @PathVariable("actionplanid") final UUID actionPlanId, final BindingResult binding)
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
    ActionPlanJob job = actionPlanJobService.createAndExecuteActionPlanJob(actionPlan);
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
