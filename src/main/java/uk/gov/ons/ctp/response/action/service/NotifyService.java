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
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify;
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify.Classifiers;
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify.Personalisation;

@Service
public class NotifyService {

  public static Logger log = LoggerFactory.getLogger(NotifyService.class);

  @Autowired private Publisher publisher;

  @Autowired private ObjectMapper objectMapper;

  public void processNotification(ActionRequest actionRequest) {

    log.with(actionRequest.getActionId()).debug("Sending notification to pubsub");

    try {
      NotifyModel notifyPayload = buildPayload(actionRequest);
      String message = objectMapper.writeValueAsString(notifyPayload);

      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      String messageId = messageIdFuture.get();
      log.with("messageId", messageId)
          .with("actionId", actionRequest.getActionId())
          .debug("Notify pubsub sent sucessfully");

      // ActionFeedback feedback = new ActionFeedback();
      // feedback.setActionId(actionRequest.getActionId());
      // feedback.setOutcome(Outcome.REQUEST_COMPLETED);
      // feedback.setSituation("Notify Email Sent");
      // feedbackService.acceptFeedback(feedback);

      // this will mimic current implementation of action -> rabbit.
      // The curent processessing service does not attempt to recover from error.
      // theres not much we can do with these checked exceptions without
      // going down the action rabbithole. So do as the current implementation
      // does and simply propagate a  RuntimeException up the stack.
    } catch (JsonProcessingException e) {
      log.error("Error converting an actionRequest to JSON", e);
      throw new RuntimeException(e);
    } catch (InterruptedException | ExecutionException e) {
      log.error("A pubsub error has occured", e);
      throw new RuntimeException(e);
    }
  }

  private NotifyModel buildPayload(ActionRequest actionRequest) {
    Classifiers classifiers =
        Classifiers.builder()
            .actionType(actionRequest.getActionType())
            .legalBasis(actionRequest.getLegalBasis())
            .region(actionRequest.getRegion())
            .surveyRef(actionRequest.getSurveyRef())
            .build();

    Personalisation personalisation =
        Personalisation.builder()
            .firstname(actionRequest.getContact().getForename())
            .lastname(actionRequest.getContact().getSurname())
            .reportingUnitReference(actionRequest.getSampleUnitRef())
            .returnByDate(actionRequest.getReturnByDate())
            .tradingSyle(actionRequest.getContact().getTradingStyle())
            .ruName(actionRequest.getContact().getRuName())
            .surveyId(actionRequest.getSurveyRef())
            .surveyName(actionRequest.getSurveyName())
            .respondentPeriod(actionRequest.getUserDescription())
            .build();

    return new NotifyModel(
        Notify.builder()
            .personalisation(personalisation)
            .classifiers(classifiers)
            .emailAddress(actionRequest.getContact().getEmailAddress())
            .build());
  }

  // public Classifiers getClassifiers(final ActionRequest actionRequest) {
  //   ClassifiersBuilder builder = Classifiers.builder();

  // }
}