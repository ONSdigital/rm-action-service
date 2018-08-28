package uk.gov.ons.ctp.response.action.service;

import static uk.gov.ons.ctp.common.time.DateTimeUtil.nowUTC;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;

@Service
public class ActionPlanJobService {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobService.class);

  public static final String CREATED_BY_SYSTEM = "SYSTEM";
  public static final String NO_ACTIONPLAN_MSG = "ActionPlan not found for id %s";

  private AppConfig appConfig;

  private ActionCaseRepository actionCaseRepo;
  private ActionPlanRepository actionPlanRepo;
  private ActionPlanJobRepository actionPlanJobRepo;

  private ActionService actionSvc;

  private DistributedLockManager actionPlanExecutionLockManager;

  public ActionPlanJobService(
      AppConfig appConfig,
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepo,
      ActionPlanJobRepository actionPlanJobRepo,
      ActionService actionSvc,
      DistributedLockManager actionPlanExecutionLockManager) {
    this.appConfig = appConfig;
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepo = actionPlanRepo;
    this.actionPlanJobRepo = actionPlanJobRepo;
    this.actionSvc = actionSvc;
    this.actionPlanExecutionLockManager = actionPlanExecutionLockManager;
  }

  @CoverageIgnore
  public ActionPlanJob findActionPlanJob(final UUID actionPlanJobId) {
    log.debug("Entering findActionPlanJob with id {}", actionPlanJobId);
    return actionPlanJobRepo.findById(actionPlanJobId);
  }

  @CoverageIgnore
  public List<ActionPlanJob> findActionPlanJobsForActionPlan(final UUID actionPlanId)
      throws CTPException {
    log.debug("Entering findActionPlanJobsForActionPlan with {}", actionPlanId);
    final ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, NO_ACTIONPLAN_MSG, actionPlanId);
    }

    return actionPlanJobRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
  }

  public void createAndExecuteAllActionPlanJobs() {
    List<ActionPlan> actionPlans = actionPlanRepo.findAll();
    actionPlans.forEach(
        actionPlan -> {
          if (shouldCreateAndExecuteActionPlanJob(actionPlan)) {
            createAndExecuteActionPlanJob(actionPlan);
          }
        });
  }

  private boolean shouldCreateAndExecuteActionPlanJob(ActionPlan actionPlan) {
    if (hasActionPlanBeenRunSinceLastSchedule(actionPlan)) {
      log.with("action_plan_id", actionPlan.getId())
          .with("action_plan_PK", actionPlan.getActionPlanPK())
          .debug("Job for plan has been run since last wake up - skipping");
      return false;
    } else if (!hasActionableCases(actionPlan)) {
      log.with("action_plan_id", actionPlan.getId())
          .with("action_plan_PK", actionPlan.getActionPlanPK())
          .debug("No actionable cases for action plan");
      return false;
    } else {
      return true;
    }
  }

  private boolean hasActionableCases(ActionPlan actionPlan) {
    return actionCaseRepo.countByActionPlanFK(actionPlan.getActionPlanPK()) > 0;
  }

  private boolean hasActionPlanBeenRunSinceLastSchedule(ActionPlan actionPlan) {
    final Date lastExecutionTime =
        new Date(nowUTC().getTime() - appConfig.getPlanExecution().getDelayMilliSeconds());
    return !(actionPlan.getLastRunDateTime() == null
        || actionPlan.getLastRunDateTime().before(lastExecutionTime));
  }

  /**
   * Create a new ActionPlanJob record and execute createActions stored procedure.
   *
   * @param actionPlan Action plan to create and execute job for
   * @return ActionPlanJob that was created or null if it has not been created.
   */
  public ActionPlanJob createAndExecuteActionPlanJob(final ActionPlan actionPlan) {

    if (!actionPlanExecutionLockManager.lock(actionPlan.getName())) {
      log.with("action_plan_id", actionPlan.getId()).debug("Could not get manager lock");
      return null;
    }

    try {
      // Action plan job has to be created before actions
      ActionPlanJob job = createActionPlanJob(actionPlan);
      actionSvc.createScheduledActions(actionPlan, job);
      return job;
    } finally {
      actionPlanExecutionLockManager.unlock(actionPlan.getName());
    }
  }

  private ActionPlanJob createActionPlanJob(final ActionPlan actionPlan) {
    ActionPlanJob actionPlanJob = new ActionPlanJob();
    actionPlanJob.setActionPlanFK(actionPlan.getActionPlanPK());
    actionPlanJob.setCreatedBy(CREATED_BY_SYSTEM);
    actionPlanJob.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
    Timestamp now = nowUTC();
    actionPlanJob.setCreatedDateTime(now);
    actionPlanJob.setUpdatedDateTime(now);
    actionPlanJob.setId(UUID.randomUUID());
    ActionPlanJob createdJob = actionPlanJobRepo.save(actionPlanJob);
    log.with("action_plan_id", actionPlan.getId().toString())
        .with("action_plan_job_id", createdJob.getId().toString())
        .debug("Created action plan job");
    return createdJob;
  }
}
