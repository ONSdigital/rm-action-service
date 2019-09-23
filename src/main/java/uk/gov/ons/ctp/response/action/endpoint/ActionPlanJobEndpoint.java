package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

/** The REST endpoint controller for ActionPlanJobs. */
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
public class ActionPlanJobEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobEndpoint.class);

  public static final String ACTION_PLAN_JOB_NOT_FOUND = "ActionPlanJob not found for id %s";
  public static final String NO_ACTIONPLAN_MSG = "ActionPlan not found for id %s";

  private final ActionPlanRepository actionPlanRepo;
  private final ActionPlanJobRepository actionPlanJobRepo;
  private final ActionPlanService actionPlanService;

  private final MapperFacade mapperFacade;

  public ActionPlanJobEndpoint(
      ActionPlanRepository actionPlanRepo,
      ActionPlanJobRepository actionPlanJobRepo,
      ActionPlanService actionPlanService,
      @Qualifier("actionBeanMapper") MapperFacade mapperFacade) {
    this.actionPlanRepo = actionPlanRepo;
    this.actionPlanJobRepo = actionPlanJobRepo;
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
    log.with("action_plan_id", actionPlanJobId).debug("Entering findActionPlanJobById");
    final ActionPlanJob actionPlanJob = actionPlanJobRepo.findById(actionPlanJobId);

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
   * @throws CTPException thrown when actionPlan cannot be found
   */
  @RequestMapping(value = "/{actionplanid}/jobs", method = RequestMethod.GET)
  public final ResponseEntity<List<ActionPlanJobDTO>> findAllActionPlanJobsByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId) throws CTPException {
    log.with("action_plan_id", actionPlanId)
        .info("Retrieving action plans jobs for given action plan id");
    final ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, NO_ACTIONPLAN_MSG, actionPlanId);
    }

    final List<ActionPlanJob> actionPlanJobs =
        actionPlanJobRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
    if (CollectionUtils.isEmpty(actionPlanJobs)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(buildActionPlanJobDTOs(actionPlanJobs, actionPlanId));
    }
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
