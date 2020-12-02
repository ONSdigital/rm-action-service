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

@Service
public class ActionPlanJobExecutor {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobExecutor.class);

  private ActionCaseRepository actionCaseRepo;
  private ActionPlanRepository actionPlanRepo;

  private ActionService actionSvc;

  public ActionPlanJobExecutor(
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepo,
      ActionService actionSvc) {
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepo = actionPlanRepo;
    this.actionSvc = actionSvc;
  }

  /**
   *
   * This method creates and executes all action plan jobs. It looks at the action plan table and cycles through
   * all of them and then if any need to be executed then it creates actions for them in a submitted state.
   *
   */
  @Transactional
  public void createAndExecuteAllActionPlanJobs() {
    List<ActionPlan> actionPlans = actionPlanRepo.findAll();
    actionPlans.forEach(this::createAndExecuteActionPlanJobs);
  }

  private void createAndExecuteActionPlanJobs(final ActionPlan actionPlan) {
    // If no cases exist in action.case table for given action plan don't create action plan job
    if (!actionCaseRepo.existsByActionPlanFK(actionPlan.getActionPlanPK())) {
      return;
    }

    log.with("name", actionPlan.getName())
        .with("action_plan_id", actionPlan.getId())
        .with("actionPlanPk", actionPlan.getActionPlanPK())
        .trace("Creating scheduled actions for plan");
    try {
      actionSvc.createScheduledActions(actionPlan.getActionPlanPK());
    } catch (Exception e) {
      log.error("Exception raised whilst creating scheduled actions for plan", e);
    }
    log.with("name", actionPlan.getName())
        .with("action_plan_id", actionPlan.getId())
        .with("actionPlanPk", actionPlan.getActionPlanPK())
        .trace("Completed creating scheduled actions for plan");
  }
}
