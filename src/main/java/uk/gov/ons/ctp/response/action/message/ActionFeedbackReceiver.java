package uk.gov.ons.ctp.response.action.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.service.FeedbackService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/**
 * The entry point for inbound feedback messages from SpringIntegration. See the
 * integration-context.xml
 *
 * <p>This is just an annotated class that acts as the initial receiver - the work is done in the
 * feedbackservice, but having this class in this package keeps spring integration related
 * entry/exit points in one logical location
 */
@CoverageIgnore
@MessageEndpoint
public class ActionFeedbackReceiver {
  private static final Logger log = LoggerFactory.getLogger(ActionFeedbackReceiver.class);

  @Autowired private FeedbackService feedbackService;

  @ServiceActivator(
      inputChannel = "actionFeedbackTransformed",
      adviceChain = "actionFeedbackRetryAdvice")
  public void acceptFeedback(final ActionFeedback feedback) throws CTPException {
    log.with("outcome", feedback.getOutcome())
        .with("action_id", feedback.getActionId())
        .info("processing action feedback");
    feedbackService.acceptFeedback(feedback);
  }
}
