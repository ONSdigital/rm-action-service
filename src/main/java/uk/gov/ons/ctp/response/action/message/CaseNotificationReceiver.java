package uk.gov.ons.ctp.response.action.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/** Message end point for Case notification life cycle messages, please see flows.xml. */
@CoverageIgnore
@MessageEndpoint
public class CaseNotificationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationReceiver.class);

  @Autowired private CaseNotificationService caseNotificationService;

  @Qualifier("genericRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired private ObjectMapper objectMapper;

  private static final String ACTION_OUTBOUND_EXCHANGE = "action-outbound-exchange";
  private static final String ACTION_ROUTING_KEY = "Action.CaseNotificationHandled.binding";

  @ServiceActivator(
      inputChannel = "caseNotificationTransformed",
      adviceChain = "caseNotificationRetryAdvice")
  public void acceptNotification(final CaseNotification caseNotification) throws CTPException {

    try {
      log.with("case_id", caseNotification.getCaseId())
          .info("Receiving case notification for case id");
      caseNotificationService.acceptNotification(caseNotification);
    } finally {
      try {
        rabbitTemplate.convertAndSend(
            ACTION_OUTBOUND_EXCHANGE,
            ACTION_ROUTING_KEY,
            objectMapper.writeValueAsString(caseNotification));
        log.with("exchange", ACTION_OUTBOUND_EXCHANGE)
            .with("case_id", caseNotification.getCaseId())
            .with("case_notification", caseNotification)
            .debug("Just wrote rabbit message to rabbit");
      } catch (JsonProcessingException e) {
        log.error("Can't send message", e);
      }
    }
  }
}
