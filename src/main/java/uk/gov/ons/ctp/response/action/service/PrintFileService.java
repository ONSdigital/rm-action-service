package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.message.UploadObjectGCS;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.printfile.Contact;
import uk.gov.ons.ctp.response.action.printfile.LetterEntry;
import uk.gov.ons.ctp.response.action.printfile.PrintFileEntry;

@Log4j
@Service
public class PrintFileService {

  @Autowired private PubSub pubSub;

  @Autowired private AppConfig appConfig;

  @Autowired private UploadObjectGCS uploadObjectGCS;

  public boolean send(String printFilename, List<ActionRequest> actionRequests) {
    boolean success = false;
    String dataFilename = FilenameUtils.removeExtension(printFilename).concat(".json");

    List<PrintFileEntry> printFile = convertToPrintFile(actionRequests);
    try {
      log.debug("creating json representation of print file");
      String json = createJsonRepresentation(printFile);
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

  private String createJsonRepresentation(List<PrintFileEntry> printFile)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(printFile);
  }

  private String createJson(List<LetterEntry> printFile) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(printFile);
  }

  protected List<PrintFileEntry> convertToPrintFile(List<ActionRequest> actionRequests) {
    List<PrintFileEntry> printFileEntries = new ArrayList<>();

    for (ActionRequest actionRequest : actionRequests) {
      PrintFileEntry entry = new PrintFileEntry();

      entry.setCaseGroupStatus(actionRequest.getCaseGroupStatus());
      entry.setIac(actionRequest.getIac());
      entry.setEnrolmentStatus(actionRequest.getEnrolmentStatus());
      entry.setRegion(actionRequest.getRegion());
      entry.setRespondentStatus(actionRequest.getRespondentStatus());
      entry.setSampleUnitRef(actionRequest.getSampleUnitRef());
      if (actionRequest.getContact() != null) {
        Contact contact = new Contact();
        contact.setEmailAddress(actionRequest.getContact().getEmailAddress());
        contact.setForename(actionRequest.getContact().getForename());
        contact.setSurname(actionRequest.getContact().getSurname());
        entry.setContact(contact);
      }
      printFileEntries.add(entry);
    }
    return printFileEntries;
  }
}
