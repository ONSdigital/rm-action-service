package uk.gov.ons.ctp.response.action.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.action.representation.CaseNotification;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.action.utility.PubSubEmulator;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(locations = "classpath:/application-test.yml")
public class CaseNotificationReceiverIT {
  private PubSubEmulator pubSubEmulator = new PubSubEmulator();
  private String file =
      "src/test/resources/uk/gov/ons/ctp/response/action/message/CaseNotificationSample.json";

  @MockBean private CaseNotificationService caseNotificationService;

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  public CaseNotificationReceiverIT() throws IOException {}

  @Test
  public void testCaseNotificationReceiverIsReceivingMessageFromPubSub() throws Exception {
    String json = readFileAsString(file);
    pubSubEmulator.publishMessage(json);
    Thread.sleep(2000);
    ObjectMapper objectMapper = new ObjectMapper();
    CaseNotification caseNotification = objectMapper.readValue(json, CaseNotification.class);
    Mockito.verify(caseNotificationService, Mockito.times(1)).acceptNotification(caseNotification);
  }

  @Test
  public void testCaseNotificationReceiverIsReceivingMultipleMessageFromPubSub() throws Exception {
    String json = readFileAsString(file);
    pubSubEmulator.publishMessage(json);
    Thread.sleep(2000);
    pubSubEmulator.publishMessage(json);
    Thread.sleep(2000);
    ObjectMapper objectMapper = new ObjectMapper();
    CaseNotification caseNotification = objectMapper.readValue(json, CaseNotification.class);
    Mockito.verify(caseNotificationService, Mockito.times(2)).acceptNotification(caseNotification);
  }

  @Test
  public void testCaseNotificationReceiverIsReceivingNoMessageFromPubSub() throws Exception {
    String json = readFileAsString(file);
    ObjectMapper objectMapper = new ObjectMapper();
    CaseNotification caseNotification = objectMapper.readValue(json, CaseNotification.class);
    Mockito.verify(caseNotificationService, Mockito.times(0)).acceptNotification(caseNotification);
  }

  private static String readFileAsString(String file) throws Exception {
    return new String(Files.readAllBytes(Paths.get(file)));
  }
}
