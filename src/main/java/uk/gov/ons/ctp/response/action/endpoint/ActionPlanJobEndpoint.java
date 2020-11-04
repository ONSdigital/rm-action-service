package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.action.scheduled.plan.ActionPlanJobExecutor;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/** The REST endpoint controller for ActionPlanJobs. */
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
public class ActionPlanJobEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanJobEndpoint.class);

  private final ActionPlanJobExecutor actionPlanJobExecutor;

  public ActionPlanJobEndpoint(ActionPlanJobExecutor actionPlanJobExecutor) {
    this.actionPlanJobExecutor = actionPlanJobExecutor;
  }

  /**
   * This method creates and executes all action plan jobs.
   *
   * <p>Note: This can only be run by one instance of action at a time. If multiple instances have
   * this invoked at the same time, duplicate actions will be created.
   *
   * @throws CTPException if no action plan job found for the specified action plan job id.
   */
  @RequestMapping(value = "/execute", method = RequestMethod.GET)
  public final ResponseEntity<String> createAndExecuteAllActionPlanJobs() throws CTPException {
    try {
      log.info("About to begin creating and executing action plan jobs");
      actionPlanJobExecutor.createAndExecuteAllActionPlanJobs();
      log.info("Completed creating and executing action plan jobs");
      return ResponseEntity.ok().body("Completed creating and executing action plan jobs");
    } catch (RuntimeException e) {
      log.error(
          "Uncaught exception - transaction rolled back. Will re-run when scheduled by cron", e);
      throw new CTPException(
          CTPException.Fault.SYSTEM_ERROR,
          "Uncaught exception when creating and execution action plan jobs");
    }
  }
}
