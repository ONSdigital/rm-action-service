package uk.gov.ons.ctp.response.action.scheduled.plan;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.service.ActionService;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedLockManager;

@Service
public class ActionPlanJobExecutor {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobExecutor.class);

  private ActionCaseRepository actionCaseRepo;
  private ActionPlanRepository actionPlanRepo;

  private ActionService actionSvc;

  private DistributedLockManager actionPlanExecutionLockManager;

  public ActionPlanJobExecutor(
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepo,
      ActionService actionSvc,
      DistributedLockManager actionPlanExecutionLockManager) {
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepo = actionPlanRepo;
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
      log.with("action_plan_id", actionPlan.getId()).info("Could not get manager lock");
      return;
    }

    try {
      // This transaction has to be committed before the lock is released, or else duplicate
      // actions will be created
      actionSvc.createScheduledActions(actionPlan.getActionPlanPK());
    } catch (Exception e) {
      log.error("Exception raised whilst creating scheduled actions", e);
    } finally {
      actionPlanExecutionLockManager.unlock(actionPlan.getName());
    }
  }
}
