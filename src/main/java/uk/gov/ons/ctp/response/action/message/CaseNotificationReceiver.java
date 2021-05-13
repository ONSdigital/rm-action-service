package uk.gov.ons.ctp.response.action.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.representation.CaseNotification;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.action.service.PubSub;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@Slf4j
@Component
public class CaseNotificationReceiver {
  @Autowired private PubSub pubSub;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CaseNotificationService caseNotificationService;

  private static ProjectSubscriptionName getPubSubSubscription(
      String project, String subscriptionId) {
    log.info(
        "creating pubsub subscription for case notification "
            + subscriptionId
            + " in project "
            + project);
    ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(project, subscriptionId);
    return subscriptionName;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void caseNotificationSubscription() throws IOException {
    // Instantiate an asynchronous message receiver.
    MessageReceiver receiver =
        (PubsubMessage message, AckReplyConsumer consumer) -> {
          // Handle incoming message, then ack the received message.
          log.info("Id: " + message.getMessageId());
          log.info("Data: " + message.getData().toStringUtf8());
          try {
            CaseNotification caseNotificationReceiver =
                objectMapper.readValue(message.getData().toStringUtf8(), CaseNotification.class);
            caseNotificationService.acceptNotification(caseNotificationReceiver);
            consumer.ack();
          } catch (final IOException | CTPException e) {
            log.error("Something went wrong");
          }
        };
    Subscriber subscriber = pubSub.getActionCaseNotificationSubscriber(receiver);
    // Start the subscriber.
    subscriber.startAsync().awaitRunning();
    log.info(
        "Listening for messages on "
            + pubSub.getActionCaseNotificationSubscriptionName().toString());
  }
}
