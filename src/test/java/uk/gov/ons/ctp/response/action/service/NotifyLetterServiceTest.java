package uk.gov.ons.ctp.response.action.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.google.api.core.ApiFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.ActionSvcApplication;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.Bucket;
import uk.gov.ons.ctp.response.action.config.GCP;
import uk.gov.ons.ctp.response.action.message.UploadObjectGCS;

@RunWith(MockitoJUnitRunner.class)
public class NotifyLetterServiceTest {
  @InjectMocks private NotifyLetterService notifyLetterService;
  @Mock private ActionSvcApplication.PubSubOutboundPrintFileGateway publisher;
  @Mock ApiFuture<String> apiFuture;
  @Mock UploadObjectGCS uploadObjectGCS;
  @Mock AppConfig appConfig;
  @Mock GCP gcp;
  @Mock Bucket bucket;

  @Test
  public void testConvertToPrintFile() throws ExecutionException, InterruptedException {
    given(apiFuture.get()).willReturn("test");
    given(uploadObjectGCS.uploadObject(anyString(), anyString(), any())).willReturn(true);
    given(appConfig.getGcp()).willReturn(gcp);
    given(gcp.getBucket()).willReturn(bucket);
    given(bucket.getName()).willReturn("test-bucket");

    notifyLetterService.processPrintFile(
        "test.csv", ProcessEventServiceTestData.buildListOfLetterEntries());

    verify(publisher).sendToPubSub(any());
    verify(uploadObjectGCS).uploadObject(anyString(), anyString(), any());
  }
}
