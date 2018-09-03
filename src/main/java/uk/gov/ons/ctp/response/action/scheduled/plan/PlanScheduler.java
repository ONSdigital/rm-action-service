package uk.gov.ons.ctp.response.action.scheduled.plan;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;

/**
 * This bean will have the actionPlanJobService injected into it by spring on constructions. It will
 * then schedule the running of the actionPlanJobService createAndExecuteAllActionPlanJobs using
 * details from the AppConfig
 */
@Component
public class PlanScheduler {
  private static final Logger log = LoggerFactory.getLogger(PlanScheduler.class);

  private final ActionPlanJobService actionPlanJobService;

  public PlanScheduler(ActionPlanJobService actionPlanJobService) {
    this.actionPlanJobService = actionPlanJobService;
  }

  /** Schedules execution of Action Plans */
  @Scheduled(fixedDelayString = "#{appConfig.planExecution.delayMilliSeconds}")
  public void run() {
    log.info("Executing ActionPlans");
    try {
      actionPlanJobService.createAndExecuteAllActionPlanJobs();
    } catch (final Exception e) {
      log.error("Exception during action plan executions", e);
    }
  }
}
