package uk.gov.ons.ctp.response.action.message;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.representation.CaseNotification;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@Component
public class CaseNotificationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationReceiver.class);
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CaseNotificationService caseNotificationService;

  /**
   * This is the message activator for the PubSub subscription which will receive the action case
   * notification
   *
   * @param message
   */
  @ServiceActivator(inputChannel = "actionCaseNotificationChannel")
  public void messageReceiver(
      Message message,
      @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage pubSubMsg) {
    log.info(
        "Receiving message ID from PubSub",
        kv("messageId", pubSubMsg.getPubsubMessage().getMessageId()));
    String payload = new String((byte[]) message.getPayload());
    log.with("payload", payload).info("New request to register action case Notification");
    try {
      log.info("Mapping payload to CaseNotification object");
      CaseNotification caseNotificationReceiver =
          objectMapper.readValue(payload, CaseNotification.class);
      log.info("Mapping successful, accepting action case notification");
      caseNotificationService.acceptNotification(caseNotificationReceiver);
      pubSubMsg.ack();
    } catch (final IOException | CTPException e) {
      log.with(e)
          .error(
              "Something went wrong while processing message received from PubSub for action case notification");
      pubSubMsg.nack();
    }
  }
}
