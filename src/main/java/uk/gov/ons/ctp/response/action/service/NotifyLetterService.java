package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.message.UploadObjectGCS;
import uk.gov.ons.ctp.response.action.printfile.LetterEntry;

@Slf4j
@Service
public class NotifyLetterService {

  @Autowired private PubSub pubSub;

  @Autowired private AppConfig appConfig;

  @Autowired private UploadObjectGCS uploadObjectGCS;

  public boolean processPrintFile(String printFilename, List<LetterEntry> printFile) {
    boolean success = false;
    String dataFilename = FilenameUtils.removeExtension(printFilename).concat(".json");
    try {
      log.debug("creating json representation of print file");
      String json = createJson(printFile);
      ByteString data = ByteString.copyFromUtf8(json);

      String bucket = appConfig.getGcp().getBucket().getName();
      log.info("about to uploaded to bucket " + bucket);
      boolean uploaded = uploadObjectGCS.uploadObject(dataFilename, bucket, data.toByteArray());

      Publisher publisher = pubSub.printfilePublisher();
      try {
        if (uploaded) {
          ByteString pubsubData = ByteString.copyFromUtf8(dataFilename);

          PubsubMessage pubsubMessage =
              PubsubMessage.newBuilder()
                  .setData(pubsubData)
                  .putAttributes("printFilename", printFilename)
                  .build();

          ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
          String messageId = messageIdFuture.get();
          log.info("print file pubsub successfully sent with messageId:" + messageId);
          success = true;
        }
      } finally {
        publisher.shutdown();
      }
    } catch (JsonProcessingException e) {
      log.error("unable to convert to json", e);
    } catch (InterruptedException | ExecutionException | IOException e) {
      log.error("pub/sub error", e);
    }
    return success;
  }

  private String createJson(List<LetterEntry> printFile) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(printFile);
  }
}
