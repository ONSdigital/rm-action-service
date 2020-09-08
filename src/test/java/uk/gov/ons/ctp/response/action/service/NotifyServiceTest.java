package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

@RunWith(MockitoJUnitRunner.class)
public class NotifyServiceTest {

  @InjectMocks private NotifyService notifyService;

  @Mock private Publisher publisher;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  @Mock private ApiFuture<String> ApiFuture;

  private static final String actionRequestJson =
      "{"
          + "\"actionId\":\"123\","
          + "\"responseRequired\":false,"
          + "\"actionPlan\":null,"
          + "\"actionType\":null,"
          + "\"questionSet\":null,"
          + "\"contact\":null,"
          + "\"address\":null,"
          + "\"legalBasis\":null,"
          + "\"region\":null,"
          + "\"respondentStatus\":null,"
          + "\"enrolmentStatus\":null,"
          + "\"caseGroupStatus\":null,"
          + "\"caseId\":null,"
          + "\"priority\":null,"
          + "\"caseRef\":null,"
          + "\"iac\":null,"
          + "\"events\":null,"
          + "\"exerciseRef\":null,"
          + "\"userDescription\":null,"
          + "\"surveyName\":null,"
          + "\"surveyRef\":null,"
          + "\"returnByDate\":null,"
          + "\"sampleUnitRef\":null}";

  @Test
  public void willCallPublisherWithEncodedJSONString() {
    ActionRequest request = new ActionRequest();
    request.setActionId("123");

    Mockito.when(publisher.publish(Mockito.any(PubsubMessage.class))).thenReturn(ApiFuture);
    notifyService.processNotification(request);

    ByteString data = ByteString.copyFromUtf8(actionRequestJson);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
    Mockito.verify(publisher).publish(pubsubMessage);
  }
}
