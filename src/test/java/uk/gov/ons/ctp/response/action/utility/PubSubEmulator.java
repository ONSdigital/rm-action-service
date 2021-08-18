package uk.gov.ons.ctp.response.action.utility;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/***
 * This is a PubSub Emulator class. This is a utility class which is used for testing pubsub function
 */
public class PubSubEmulator {
  private static final Logger log = LoggerFactory.getLogger(PubSubEmulator.class);
  private static final String HOST_PORT = "localhost:18681";
  public static final ManagedChannel CHANNEL =
      ManagedChannelBuilder.forTarget(HOST_PORT).usePlaintext().build();
  public static final TransportChannelProvider CHANNEL_PROVIDER =
      FixedTransportChannelProvider.create(GrpcTransportChannel.create(CHANNEL));
  public static final CredentialsProvider CREDENTIAL_PROVIDER = NoCredentialsProvider.create();
  private static final String PROJECT_ID = "test";
  private static final String TOPIC_ID = "test_topic";
  private static final String SUBSCRIPTION_ID = "test_subscription";

  public PubSubEmulator() throws IOException {}

  public Publisher getEmulatorPublisher() throws IOException {
    TopicName topicName = TopicName.of(PROJECT_ID, TOPIC_ID);
    return Publisher.newBuilder(topicName)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public Subscriber getEmulatorSubscriber(MessageReceiver receiver) {
    return Subscriber.newBuilder(ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID), receiver)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public void publishMessage(String message) {
    try {
      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
      Publisher publisher = getEmulatorPublisher();
      log.with("publisher", publisher).info("Publishing message to pubsub emulator");
      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      String messageId = messageIdFuture.get();
      log.with("messageId", messageId).info("Published message to pubsub emulator");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }
}
