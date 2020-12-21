package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.config.AppConfig;

@Service
public class NotifyEmailService {
  public static Logger log = LoggerFactory.getLogger(NotifyEmailService.class);

  @Autowired AppConfig appConfig;

  @Autowired private PubSub pubSub;

  @Autowired private ObjectMapper objectMapper;

  public void processEmail(NotifyModel notifyPayload) {

    log.debug("Sending notification to pubsub");

    try {
      String message = objectMapper.writeValueAsString(notifyPayload);

      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      Publisher publisher = pubSub.notifyPublisher();
      try {

        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        String messageId = messageIdFuture.get();
        log.with("messageId", messageId).debug("Notify pubsub sent successfully");
      } finally {
        publisher.shutdown();
      }
    } catch (JsonProcessingException e) {
      log.error("Error converting an actionRequest to JSON", e);
      throw new RuntimeException(e);
    } catch (InterruptedException | ExecutionException | IOException e) {
      log.error("A pubsub error has occured", e);
      throw new RuntimeException(e);
    }
  }
}
