package uk.gov.ons.ctp.response.action.service;

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.utility.PubSubEmulator;

@Slf4j
@Component
public class PubSub {

  @Autowired AppConfig appConfig;

  /**
   * Private method to create Publisher against project and topic
   *
   * @param project:String project id
   * @param topic: String topic name
   * @return com.google.cloud.pubsub.v1.Publisher;
   * @throws IOException
   */
  private Publisher publisherSupplier(String project, String topic) throws IOException {
    log.info("creating pubsub publish for topic " + topic + " in project " + project);
    TopicName topicName = TopicName.of(project, topic);
    return Publisher.newBuilder(topicName).build();
  }

  /***
   * Creates notify publisher used for emails notifications
   * @return com.google.cloud.pubsub.v1.Publisher;
   * @throws IOException
   */
  public Publisher notifyPublisher() throws IOException {
    return publisherSupplier(appConfig.getGcp().getProject(), appConfig.getGcp().getNotifyTopic());
  }

  /***
   * Creates print file publisher used for print file
   * @return com.google.cloud.pubsub.v1.Publisher;
   * @throws IOException
   */
  public Publisher printfilePublisher() throws IOException {
    return publisherSupplier(
        appConfig.getGcp().getProject(), appConfig.getGcp().getPrintFileTopic());
  }

  /***
   * Provides subscription name for the case notification subscriber
   * @return com.google.pubsub.v1.ProjectSubscriptionName
   */
  public ProjectSubscriptionName getActionCaseNotificationSubscriptionName() {
    String project = appConfig.getGcp().getProject();
    String subscriptionId = appConfig.getGcp().getCaseNotificationSubscription();
    log.info(
        "creating pubsub subscription name for case notification "
            + subscriptionId
            + " in project "
            + project);
    ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(project, subscriptionId);
    return subscriptionName;
  }

  /**
   * Provides PubSub subscriber for case notification against message receiver
   *
   * @param receiver: com.google.cloud.pubsub.v1.MessageReceiver;
   * @return com.google.cloud.pubsub.v1.Subscriber;
   */
  public Subscriber getActionCaseNotificationSubscriber(MessageReceiver receiver)
      throws IOException {
    if (null == System.getenv("PUBSUB_EMULATOR_HOST")) {
      log.info("Returning Subscriber");
      ExecutorProvider executorProvider =
          InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(4).build();
      // `setParallelPullCount` determines how many StreamingPull streams the subscriber will open
      // to receive message. It defaults to 1. `setExecutorProvider` configures an executor for the
      // subscriber to process messages. Here, the subscriber is configured to open 2 streams for
      // receiving messages, each stream creates a new executor with 4 threads to help process the
      // message callbacks. In total 2x4=8 threads are used for message processing.
      return Subscriber.newBuilder(getActionCaseNotificationSubscriptionName(), receiver)
          .setParallelPullCount(2)
          .setExecutorProvider(executorProvider)
          .build();
    } else {
      log.info("Returning emulator Subscriber");
      return new PubSubEmulator().getEmulatorSubscriber(receiver);
    }
  }
}
