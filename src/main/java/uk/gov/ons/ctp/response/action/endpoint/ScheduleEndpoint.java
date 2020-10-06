package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.service.ActionService;
import uk.gov.ons.ctp.response.lib.common.distributed.DistributedLockManager;

@RestController
@RequestMapping(value = "/schedule", produces = "application/json")
public class ScheduleEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ScheduleEndpoint.class);

  private ActionCaseRepository actionCaseRepo;
  private ActionPlanRepository actionPlanRepo;

  private ActionService actionSvc;

  private DistributedLockManager actionPlanExecutionLockManager;

  public ScheduleEndpoint(
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
  @RequestMapping(value = "/action-plans", method = RequestMethod.POST)
  public ResponseEntity<Void> createAndExecuteAllActionPlanJobs() {
    List<ActionPlan> actionPlans = actionPlanRepo.findAll();
    actionPlans.forEach(this::createAndExecuteActionPlanJobs);
    return ResponseEntity.accepted().body(null);
  }

  @Async
  public CompletableFuture<Void> createAndExecuteActionPlanJobs(final ActionPlan actionPlan) {
    // If no cases exist in action.case table for given action plan don't create action plan job
    if (!actionCaseRepo.existsByActionPlanFK(actionPlan.getActionPlanPK())) {
      return CompletableFuture.completedFuture(null);
    }
    try {
      actionSvc.createScheduledActions(actionPlan.getActionPlanPK());
    } catch (Exception e) {
      log.error("Exception raised whilst creating scheduled actions", e);
    } finally {
      return CompletableFuture.completedFuture(null);
    }
  }
}
