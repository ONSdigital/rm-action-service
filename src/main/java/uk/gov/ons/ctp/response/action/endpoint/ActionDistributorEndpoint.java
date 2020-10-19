package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.action.scheduled.distribution.ActionDistributor;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/** The REST endpoint controller for ActionDistributor. */
@RestController
@RequestMapping(value = "/distribute", produces = "application/json")
public class ActionDistributorEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionDistributorEndpoint.class);

  private final ActionDistributor actionDistributor;

  public ActionDistributorEndpoint(ActionDistributor actionDistributor) {
    this.actionDistributor = actionDistributor;
  }

  /**
   * Calling this invokes the distribution of actions to either ras-rm-notify or the
   * ras-rm-print-file to create either emails or letters.
   *
   * @throws CTPException on any exception thrown
   */
  @RequestMapping(method = RequestMethod.GET)
  public final ResponseEntity<String> distributeActions() throws CTPException {
    try {
      log.info("About to begin distribution");
      actionDistributor.distribute();
      log.info("Completed distribution");
      return ResponseEntity.ok().body("Completed distribution");
    } catch (RuntimeException e) {
      log.error(
          "Uncaught exception - transaction rolled back. Will re-run when scheduled by cron", e);
      throw new CTPException(
          CTPException.Fault.SYSTEM_ERROR, "Uncaught exception when exporting print file");
    }
  }
}
