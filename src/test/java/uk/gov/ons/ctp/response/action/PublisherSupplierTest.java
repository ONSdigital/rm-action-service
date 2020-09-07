package uk.gov.ons.ctp.response.action;

import static org.junit.Assert.assertEquals;

import com.google.cloud.pubsub.v1.Publisher;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.PubsubConfig.PublisherSupplier;

@RunWith(MockitoJUnitRunner.class)
public class PublisherSupplierTest {

  @Mock private Publisher p;

  @Test
  public void willSupplyAPublisher() throws IOException {
    PublisherSupplier supplier = () -> p;
    assertEquals(p, supplier.get());
  }
}
