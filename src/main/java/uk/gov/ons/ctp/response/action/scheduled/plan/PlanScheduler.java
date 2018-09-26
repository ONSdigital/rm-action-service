package uk.gov.ons.ctp.response.action.scheduled.plan;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;

/**
 * This bean will have the actionPlanJobService injected into it by spring on constructions. It will
 * then schedule the running of the actionPlanJobService createAndExecuteAllActionPlanJobs using
 * details from the AppConfig
 */
@Component
public class PlanScheduler implements HealthIndicator {
  private static final Logger log = LoggerFactory.getLogger(PlanScheduler.class);

  @Autowired private ActionPlanJobService actionPlanJobServiceImpl;

  private PlanExecutionInfo executionInfo = new PlanExecutionInfo();

  /**
   * schedule the Execution of Action Plans It is simply a scheduled trigger for the service layer
   * method.
   */
  @Scheduled(fixedDelayString = "#{appConfig.planExecution.delayMilliSeconds}")
  public void run() {
    try {
      executionInfo = new PlanExecutionInfo();
      executionInfo.setExecutedJobs(actionPlanJobServiceImpl.createAndExecuteAllActionPlanJobs());
    } catch (final Exception e) {
      log.error("Exception in action plan scheduler", e);
    }
  }

  @Override
  public Health health() {
    return Health.up().withDetail("planExecutionInfo", executionInfo).build();
  }
}
