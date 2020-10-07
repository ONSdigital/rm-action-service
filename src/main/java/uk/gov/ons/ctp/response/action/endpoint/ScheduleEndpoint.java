package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

@RestController
@RequestMapping(value = "/schedule", produces = "application/json")
public class ScheduleEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ScheduleEndpoint.class);

  private ActionPlanService actionPlanService;

  @Autowired
  public ScheduleEndpoint(ActionPlanService actionPlanService) {
    this.actionPlanService = actionPlanService;
  }

  /**
   * On a schedule create a new ActionPlanJob and create associated actions for all action plans
   * with cases in the action.case table
   */
  @RequestMapping(value = "/actionplans", method = RequestMethod.POST)
  public ResponseEntity<Void> createAndExecuteAllActionPlanJobs() {
    log.info("request received to execute all action plans");
    execute();
    return ResponseEntity.accepted().body(null);
  }

  @Async
  public CompletableFuture<Void> execute() {
    actionPlanService.executeAllActionPlans();
    return CompletableFuture.completedFuture(null);
  }
}
