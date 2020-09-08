package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

@Service
public class NotifyService {

  public static Logger log = LoggerFactory.getLogger(NotifyService.class);

  @Autowired private Publisher publisher;

  @Autowired private ObjectMapper objectMapper;

  public void processNotification(ActionRequest actionRequest) {

    log.with(actionRequest.getActionId()).debug("Sending notification to pubsub");

    try {
      String message = objectMapper.writeValueAsString(actionRequest);

      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      String messageId = messageIdFuture.get();
      log.with("messageId", messageId)
          .with("actionId", actionRequest.getActionId())
          .debug("Notify pubsub sent sucessfully");

      // this will mimic current implementation of action -> rabbit.
      // The curent processessing service does not attempt to recover from error
      // when sending a message to rabbit, theres not much we can do with these checked exceptions
      // without going down the action rabbithole and i value my sanity too much for that,
      // so do as the current implementation does and simply throw a Runtime exception up the chain.
    } catch (JsonProcessingException e) {
      log.error("Error converting an actionRequest to JSON", e);
      throw new RuntimeException(e);
    } catch (InterruptedException | ExecutionException e) {
      log.error("A pubsub error has occured", e);
      throw new RuntimeException(e);
    }
  }
}
