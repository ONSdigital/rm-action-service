package uk.gov.ons.ctp.response.action.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static uk.gov.ons.ctp.common.time.DateTimeUtil.nowUTC;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    log.with("action_plan_job_id", actionPlanJobId).debug("Entering findActionPlanJob");
    return actionPlanJobRepo.findById(actionPlanJobId);
  }

  @CoverageIgnore
  public List<ActionPlanJob> findActionPlanJobsForActionPlan(final UUID actionPlanId)
      throws CTPException {
    log.with("action_plan_id", actionPlanId).debug("Entering findActionPlanJobsForActionPlan");
    final ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, NO_ACTIONPLAN_MSG, actionPlanId);
    }

    return actionPlanJobRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
  }

  /**
   * Create a new ActionPlanJob and create associated actions for all action plans with cases in the
   * action.case table
   */
  @Transactional(propagation = REQUIRES_NEW)
  public void createAndExecuteAllActionPlanJobs() {
    List<ActionPlan> actionPlans = actionPlanRepo.findAll();
    actionPlans.forEach(
        actionPlan -> {
          if (hasActionableCases(actionPlan)) {
            createAndExecuteActionPlanJob(actionPlan);
          }
        });
  }

  private boolean hasActionableCases(ActionPlan actionPlan) {
    return actionCaseRepo.countByActionPlanFK(actionPlan.getActionPlanPK()) > 0;
  }

  private void createAndExecuteActionPlanJob(final ActionPlan actionPlan) {
    if (!actionPlanExecutionLockManager.lock(actionPlan.getName())) {
      log.with("action_plan_id", actionPlan.getId()).debug("Could not get manager lock");
      return;
    }

    try {
      // Action plan job has to be created before actions
      ActionPlanJob job = createActionPlanJob(actionPlan);
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
