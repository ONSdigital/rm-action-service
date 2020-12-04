package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.action.scheduled.distribution.ActionProcessor;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/** The REST endpoint controller for ActionDistributor. */
@RestController
@RequestMapping(value = "/process", produces = "application/json")
public class ActionProcessorEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionProcessorEndpoint.class);

  private final ActionProcessor actionProcessor;

  public ActionProcessorEndpoint(ActionProcessor actionProcessor) {
    this.actionProcessor = actionProcessor;
  }

  /**
   * Calling this invokes the distribution of actions to either ras-rm-notify or the
   * ras-rm-print-file to create either emails or letters.
   *
   * @throws CTPException on any exception thrown
   */
  @RequestMapping(value = "/emails", method = RequestMethod.GET)
  public final ResponseEntity<String> emails() throws CTPException {
    try {
      log.info("About to begin distribution");
      actionProcessor.processEmails();
      log.info("Completed distribution");
      return ResponseEntity.ok().body("Completed distribution");
    } catch (RuntimeException e) {
      log.error(
          "Uncaught exception - transaction rolled back. Will re-run when scheduled by cron", e);
      throw new CTPException(
          CTPException.Fault.SYSTEM_ERROR, "Uncaught exception when exporting print file");
    }
  }

  /**
   * Calling this invokes the distribution of actions to either ras-rm-notify or the
   * ras-rm-print-file to create either emails or letters.
   *
   * @throws CTPException on any exception thrown
   */
  @RequestMapping(value = "/letters", method = RequestMethod.GET)
  public final ResponseEntity<String> processLetters() throws CTPException {
    try {
      log.info("About to begin distribution");
      actionProcessor.processLetters();
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
