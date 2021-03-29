package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotifyEmailServiceTest {

  @InjectMocks private NotifyEmailService notifyEmailService;

  @Mock private PubSub pubSub;

  @Mock private Publisher publisher;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  @Mock private com.google.api.core.ApiFuture<String> ApiFuture;

  private static final String emailJson =
      "{\"notify\":{"
          + "\"email_address\":null,"
          + "\"classifiers\":{"
          + "\"survey\":\"\","
          + "\"region\":\"\""
          + "},"
          + "\"personalisation\":{"
          + "\"reporting unit reference\":null,"
          + "\"survey id\":null,"
          + "\"survey name\":null,"
          + "\"firstname\":\"Joe\","
          + "\"lastname\":null,"
          + "\"return by date\":null,"
          + "\"RU name\":null,"
          + "\"trading style\":null,"
          + "\"respondent period\":null"
          + "}"
          + "}}";

  @Test
  public void willCallPublisherWithEncodedJSONString() throws IOException {
    NotifyModel.Notify.Classifiers classifiers =
        NotifyModel.Notify.Classifiers.builder().region("").surveyRef("").build();

    NotifyModel.Notify.Personalisation personalisation =
        NotifyModel.Notify.Personalisation.builder()
            .firstname("Joe")
            .lastname(null)
            .reportingUnitReference(null)
            .returnByDate(null)
            .tradingSyle(null)
            .ruName(null)
            .surveyId(null)
            .surveyName(null)
            .respondentPeriod(null)
            .build();

    Mockito.when(pubSub.notifyPublisher()).thenReturn(publisher);
    Mockito.when(publisher.publish(Mockito.any(PubsubMessage.class))).thenReturn(ApiFuture);
    notifyEmailService.processEmail(
        new NotifyModel(
            NotifyModel.Notify.builder()
                .personalisation(personalisation)
                .classifiers(classifiers)
                .emailAddress(null)
                .build()));

    ByteString data = ByteString.copyFromUtf8(emailJson);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
    Mockito.verify(publisher).publish(pubsubMessage);
  }
}
