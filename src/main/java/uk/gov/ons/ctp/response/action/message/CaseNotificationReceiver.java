package uk.gov.ons.ctp.response.action.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.representation.CaseNotification;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.action.service.PubSub;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@Component
public class CaseNotificationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationReceiver.class);
  @Autowired private PubSub pubSub;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CaseNotificationService caseNotificationService;

  //  private static ProjectSubscriptionName getPubSubSubscription(
  //      String project, String subscriptionId) {
  //    log.with(subscriptionId, project)
  //        .info("creating pubsub subscription for case notification with subscriptionId in
  // project");
  //    ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(project,
  // subscriptionId);
  //    return subscriptionName;
  //  }
  //
  //  /**
  //   * This method provides an Event Listener subscription for the action case notification.
  //   *
  //   * @returns void
  //   * @throws IOException
  //   */
  //  @EventListener(ApplicationReadyEvent.class)
  //  public void caseNotificationSubscription() throws IOException {
  //    // Instantiate an asynchronous message receiver.
  //    MessageReceiver receiver =
  //        (PubsubMessage message, AckReplyConsumer consumer) -> {
  //          // Handle incoming message, then ack the received message.
  //          log.with(message.getMessageId()).info("Receiving message ID from PubSub");
  //          log.with(message.getData().toString()).debug("Receiving data from PubSub ");
  //          try {
  //            CaseNotification caseNotificationReceiver =
  //                objectMapper.readValue(message.getData().toStringUtf8(),
  // CaseNotification.class);
  //            caseNotificationService.acceptNotification(caseNotificationReceiver);
  //            consumer.ack();
  //          } catch (final IOException | CTPException e) {
  //            log.with(e)
  //                .error(
  //                    "Something went wrong while processing message received from PubSub for case
  // notification");
  //            consumer.nack();
  //          }
  //        };
  //    Subscriber subscriber = pubSub.getActionCaseNotificationSubscriber(receiver);
  //    // Start the subscriber.
  //    subscriber.startAsync().awaitRunning();
  //    log.with(pubSub.getActionCaseNotificationSubscriptionName().toString())
  //        .info("Listening for action case notification messages on PubSub-subscription id");
  //  }

  @ServiceActivator(inputChannel = "actionCaseNotificationChannel")
  public void messageReceiver(Message message) {
    String payload = new String((byte[]) message.getPayload());
    log.info("Message arrived! Payload: " + payload);
    BasicAcknowledgeablePubsubMessage originalMessage =
        message
            .getHeaders()
            .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);

    try {
      CaseNotification caseNotificationReceiver =
          objectMapper.readValue(payload, CaseNotification.class);
      caseNotificationService.acceptNotification(caseNotificationReceiver);
    } catch (final IOException | CTPException e) {
      log.with(e)
          .error(
              "Something went wrong while processing message received from PubSub for case notification");
    }
  }
}
