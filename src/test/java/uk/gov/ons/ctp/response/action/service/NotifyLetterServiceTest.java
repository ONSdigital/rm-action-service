package uk.gov.ons.ctp.response.action.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.Bucket;
import uk.gov.ons.ctp.response.action.config.GCP;
import uk.gov.ons.ctp.response.action.message.UploadObjectGCS;

@RunWith(MockitoJUnitRunner.class)
public class NotifyLetterServiceTest {
  @InjectMocks private NotifyLetterService notifyLetterService;
  @Mock private Publisher publisher;
  @Mock ApiFuture<String> apiFuture;
  @Mock UploadObjectGCS uploadObjectGCS;
  @Mock AppConfig appConfig;
  @Mock GCP gcp;
  @Mock Bucket bucket;
  @Mock private PubSub pubSub;

  @Test
  public void testConvertToPrintFile()
      throws IOException, ExecutionException, InterruptedException {
    when(pubSub.printfilePublisher()).thenReturn(publisher);
    given(publisher.publish(any())).willReturn(apiFuture);
    given(apiFuture.get()).willReturn("test");
    given(uploadObjectGCS.uploadObject(anyString(), anyString(), any())).willReturn(true);
    given(appConfig.getGcp()).willReturn(gcp);
    given(gcp.getBucket()).willReturn(bucket);
    given(bucket.getName()).willReturn("test-bucket");

    notifyLetterService.processPrintFile(
        "test.csv", ProcessEventServiceTestData.buildListOfLetterEntries());

    verify(publisher).publish(any());
    verify(apiFuture).get();
    verify(uploadObjectGCS).uploadObject(anyString(), anyString(), any());
  }
}
