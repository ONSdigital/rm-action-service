package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.Bucket;
import uk.gov.ons.ctp.response.action.config.GCP;
import uk.gov.ons.ctp.response.action.domain.model.ActionRequestInstruction;
import uk.gov.ons.ctp.response.action.message.UploadObjectGCS;
import uk.gov.ons.ctp.response.action.printfile.PrintFileEntry;

/** */
@RunWith(MockitoJUnitRunner.class)
public class PrintFileServiceTest {

  @Mock private Publisher publisher;
  @Mock ApiFuture<String> apiFuture;
  @Mock UploadObjectGCS uploadObjectGCS;
  @Mock AppConfig appConfig;
  @Mock GCP gcp;
  @Mock Bucket bucket;
  @Mock private PubSub pubSub;

  @InjectMocks private PrintFileService printFileService;

  @Test
  public void testConvertToPrintFile() {
    List<ActionRequestInstruction> actionRequestInstructions =
        ObjectBuilder.buildListOfActionRequests();

    List<PrintFileEntry> printFileEntries =
        printFileService.convertToPrintFile(actionRequestInstructions);

    assertFalse(printFileEntries.isEmpty());

    for (PrintFileEntry entry : printFileEntries) {
      assertEquals("testIac", entry.getIac());
      assertEquals("testCaseGroupStatus", entry.getCaseGroupStatus());
      assertEquals("testEnrolmentStatus", entry.getEnrolmentStatus());
      assertEquals("testRegion", entry.getRegion());
      assertEquals("testRespondentStatus", entry.getRespondentStatus());
      assertEquals("testSampleUnitRef", entry.getSampleUnitRef());
      assertEquals("testEmailAddress", entry.getContact().getEmailAddress());
      assertEquals("testForename", entry.getContact().getForename());
      assertEquals("testSurname", entry.getContact().getSurname());
    }
  }

  @Test
  public void testSend() throws Exception {

    when(pubSub.printfilePublisher()).thenReturn(publisher);
    given(publisher.publish(any())).willReturn(apiFuture);
    given(apiFuture.get()).willReturn("test");
    given(uploadObjectGCS.uploadObject(anyString(), anyString(), any())).willReturn(true);
    given(appConfig.getGcp()).willReturn(gcp);
    given(gcp.getBucket()).willReturn(bucket);
    given(bucket.getName()).willReturn("test-bucket");

    List<ActionRequestInstruction> actionRequestInstructions =
        ObjectBuilder.buildListOfActionRequests();

    printFileService.send("test.csv", actionRequestInstructions);

    verify(publisher).publish(any());
    verify(apiFuture).get();
    verify(uploadObjectGCS).uploadObject(anyString(), anyString(), any());
  }
}
