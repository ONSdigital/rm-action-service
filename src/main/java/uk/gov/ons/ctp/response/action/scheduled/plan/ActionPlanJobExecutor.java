package uk.gov.ons.ctp.response.action.scheduled.plan;

import static uk.gov.ons.ctp.common.time.DateTimeUtil.nowUTC;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.service.ActionService;

@Service
public class ActionPlanJobExecutor {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobExecutor.class);

  public static final String CREATED_BY_SYSTEM = "SYSTEM";

  private ActionCaseRepository actionCaseRepo;
  private ActionPlanRepository actionPlanRepo;
  private ActionPlanJobRepository actionPlanJobRepo;

  private ActionService actionSvc;

  private DistributedLockManager actionPlanExecutionLockManager;

  public ActionPlanJobExecutor(
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepo,
      ActionPlanJobRepository actionPlanJobRepo,
      ActionService actionSvc,
      DistributedLockManager actionPlanExecutionLockManager) {
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepo = actionPlanRepo;
    this.actionPlanJobRepo = actionPlanJobRepo;
    this.actionSvc = actionSvc;
    this.actionPlanExecutionLockManager = actionPlanExecutionLockManager;
  }

  /**
   * On a schedule create a new ActionPlanJob and create associated actions for all action plans
   * with cases in the action.case table
   */
  @Transactional
  @Scheduled(fixedDelayString = "#{appConfig.planExecution.delayMilliSeconds}")
  public void createAndExecuteAllActionPlanJobs() {
    List<ActionPlan> actionPlans = actionPlanRepo.findAll();
    actionPlans.forEach(this::createAndExecuteActionPlanJobs);
  }

  private void createAndExecuteActionPlanJobs(final ActionPlan actionPlan) {
    // If no cases exist in action.case table for given action plan don't create action plan job
    if (!actionCaseRepo.existsByActionPlanFK(actionPlan.getActionPlanPK())) {
      return;
    }

    if (!actionPlanExecutionLockManager.lock(actionPlan.getName())) {
      log.with("action_plan_id", actionPlan.getId()).debug("Could not get manager lock");
      return;
    }

    try {
      // Action plan job has to be created before actions
      ActionPlanJob job = createActionPlanJob(actionPlan);

      // This transaction has to be committed before the lock is released, or else duplicate
      // actions will be created
      actionSvc.createScheduledActions(actionPlan, job);
    } finally {
      actionPlanExecutionLockManager.unlock(actionPlan.getName());
    }
  }

  private ActionPlanJob createActionPlanJob(final ActionPlan actionPlan) {
    ActionPlanJob actionPlanJob = new ActionPlanJob();
    actionPlanJob.setActionPlanFK(actionPlan.getActionPlanPK());
    actionPlanJob.setCreatedBy(CREATED_BY_SYSTEM);
    actionPlanJob.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
    final Timestamp now = nowUTC();
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
