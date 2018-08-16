package uk.gov.ons.ctp.response.action.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/** Message end point for Case notification life cycle messages, please see flows.xml. */
@CoverageIgnore
@MessageEndpoint
@Slf4j
public class CaseNotificationReceiver {

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
      log.debug("Receiving case notification for case id {}", caseNotification.getCaseId());
      caseNotificationService.acceptNotification(caseNotification);
    } finally {
      try {
        rabbitTemplate.convertAndSend(
            ACTION_OUTBOUND_EXCHANGE,
            ACTION_ROUTING_KEY,
            objectMapper.writeValueAsString(caseNotification));
      } catch (JsonProcessingException e) {
        log.error("Can't send message: " + e.toString());
      }
    }
  }
}
