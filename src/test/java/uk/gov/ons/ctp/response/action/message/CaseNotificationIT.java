package uk.gov.ons.ctp.response.action.message;

import java.util.UUID;
import javax.xml.bind.JAXBContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.lib.common.utility.Mapzer;
import uk.gov.ons.ctp.response.lib.rabbit.Rabbitmq;
import uk.gov.ons.ctp.response.lib.rabbit.SimpleMessageListener;
import uk.gov.ons.ctp.response.lib.rabbit.SimpleMessageSender;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CaseNotificationIT {
  @Autowired private ResourceLoader resourceLoader;
  @Autowired private AppConfig appConfig;

  @Test
  public void ensureSampleUnitIdReceived() throws Exception {
    // sendCaseNotification();
  }

  private void sendCaseNotification() throws Exception {
    CaseNotification caseNotification = new CaseNotification();
    caseNotification.setSampleUnitId(String.valueOf(UUID.randomUUID()));
    caseNotification.setExerciseId(String.valueOf(UUID.randomUUID()));
    caseNotification.setPartyId(String.valueOf(UUID.randomUUID()));
    caseNotification.setSampleUnitType("H");
    caseNotification.setNotificationType(NotificationType.ACTIVATED);
    caseNotification.setCaseId("2");

    JAXBContext jaxbContext = JAXBContext.newInstance(CaseNotification.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(
                jaxbContext, caseNotification, "casesvc/xsd/outbound/caseNotification.xsd");
    getMessageSender().sendMessageToQueue("Case.LifecycleEvents", xml);
  }

  private SimpleMessageSender getMessageSender() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageSender(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }

  private SimpleMessageListener getMessageListener() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageListener(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }
}
