package uk.gov.ons.ctp.response.action.endpoint;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Java6Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import javax.xml.bind.JAXBContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanPostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPostRequestDTO;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageBase;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

/** Integration tests for action endpoints */
@Slf4j
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionEndpointIT {

  @Autowired private ResourceLoader resourceLoader;

  @LocalServerPort private int port;

  @Autowired private ObjectMapper mapper;

  @Autowired private AppConfig appConfig;

  @Rule public WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

  private Mapzer mapzer;

  @Before
  public void setup() {
    mapzer = new Mapzer(resourceLoader);
    initialiseUnirestObjectMapper();
  }

  @Test
  public void ensureSampleIDExposed() throws Exception {
    UUID collexId = UUID.fromString("3116a1bd-3a84-4761-ae30-4916c4e7120a");
    UUID sampleUnitId = UUID.randomUUID();

    createCollectionExerciseMock(collexId);

    SimpleMessageSender sender = getMessageSender();

    ActionPlanPostRequestDTO ap = new ActionPlanPostRequestDTO();
    ap.setCreatedBy("SYSTEM");
    ap.setName("action-test: " + new Random().nextInt(100));
    ap.setDescription("just testing!");

    HttpResponse<ActionPlanDTO> createActionPlanRes =
        Unirest.post("http://localhost:" + this.port + "/actionplans")
            .basicAuth("admin", "secret")
            .header("accept", "application/json")
            .header("Content-Type", "application/json")
            .body(ap)
            .asObject(ActionPlanDTO.class);

    assertThat(createActionPlanRes.getStatus()).isEqualTo(201);

    CaseDetailsDTO case_details_dto =
        createCaseDetailsMock(collexId, createActionPlanRes.getBody().getId());

    createCaseEventMock(case_details_dto.getId());

    SampleAttributesDTO sample_attributes = createSampleAttributesMock(sampleUnitId);

    createSurveyDetailsMock();

    CaseNotification casenot = new CaseNotification();

    casenot.setSampleUnitId(sampleUnitId.toString());
    casenot.setCaseId(case_details_dto.getId().toString());
    casenot.setActionPlanId(case_details_dto.getActionPlanId().toString());
    casenot.setExerciseId(collexId.toString());
    casenot.setNotificationType(NotificationType.ACTIVATED);
    casenot.setSampleUnitType("H");

    JAXBContext jaxbContext = JAXBContext.newInstance(CaseNotification.class);
    SimpleMessageListener listener = getMessageListener();
    BlockingQueue<String> queue =
        listener.listen(
            SimpleMessageBase.ExchangeType.Direct,
            "action-outbound-exchange",
            "Action.CaseNotificationHandled.binding");

    BlockingQueue<String> queue2 =
        listener.listen(
            SimpleMessageBase.ExchangeType.Direct,
            "action-outbound-exchange",
            "Action.Printer.binding");

    String xml =
        mapzer.convertObjectToXml(
            jaxbContext, casenot, "casesvc/xsd/outbound/caseNotification.xsd");

    sender.sendMessageToQueue("Case.LifecycleEvents", xml);

    String message = queue.take();

    assertThat(message).isNotNull();

    ActionPostRequestDTO apord = new ActionPostRequestDTO();
    apord.setCaseId(UUID.fromString(casenot.getCaseId()));
    apord.setCreatedBy("SYSTEM");
    apord.setActionTypeName("SOCIALNOT");
    apord.setPriority(1);

    Unirest.post("http://localhost:" + this.port + "/actions")
        .basicAuth("admin", "secret")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(apord)
        .asObject(ActionPostRequestDTO.class);

    String printer_message = queue2.take();

    assertThat(printer_message).isNotNull();

    JAXBContext xmlToObject = JAXBContext.newInstance(ActionInstruction.class);
    ActionInstruction acti =
        (ActionInstruction)
            xmlToObject
                .createUnmarshaller()
                .unmarshal(new ByteArrayInputStream(printer_message.getBytes()));

    ActionAddress address = acti.getActionRequest().getAddress();

    log.debug("printer_message = " + printer_message);

    assertThat(address.getSampleUnitRef())
        .isEqualTo(sample_attributes.getAttributes().get("Reference"));
    assertThat(address.getLine1()).isEqualTo(sample_attributes.getAttributes().get("Prem1"));
    assertThat(address.getPostcode()).isEqualTo(sample_attributes.getAttributes().get("Postcode"));
    assertThat(address.getTownName()).isEqualTo(sample_attributes.getAttributes().get("PostTown"));
  }

  private String loadResourceAsString(Class clazz, String resourceName) throws IOException {
    InputStream is = clazz.getResourceAsStream(resourceName);
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, StandardCharsets.UTF_8.name());
    return writer.toString();
  }

  /**
   * Creates a new SimpleMessageSender based on the config in AppConfig
   *
   * @return a new SimpleMessageSender
   */
  private SimpleMessageSender getMessageSender() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageSender(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }

  /**
   * Creates a new SimpleMessageListener based on the config in AppConfig
   *
   * @return a new SimpleMessageListener
   */
  private SimpleMessageListener getMessageListener() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageListener(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }

  private void initialiseUnirestObjectMapper() {
    Unirest.setObjectMapper(
        new com.mashape.unirest.http.ObjectMapper() {
          public <T> T readValue(final String value, final Class<T> valueType) {
            try {
              return mapper.readValue(value, valueType);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }

          public String writeValue(final Object value) {
            try {
              return mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  private void createCollectionExerciseMock(UUID collexID) throws IOException {
    String f =
        loadResourceAsString(ActionEndpointIT.class, "ActionEndpointIT.CollectionExercise.json");

    this.wireMockRule.stubFor(
        get(urlPathEqualTo(String.format("/collectionexercises/%s", collexID.toString())))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(String.format(f, collexID.toString()))));
  }

  private CaseDetailsDTO createCaseDetailsMock(UUID collexId, UUID actionPlanId)
      throws IOException {
    String f = loadResourceAsString(ActionEndpointIT.class, "ActionEndpointIT.CaseDetailsDTO.json");
    String case_details = String.format(f, actionPlanId, collexId);
    CaseDetailsDTO case_details_dto = mapper.readValue(case_details, CaseDetailsDTO.class);

    this.wireMockRule.stubFor(
        get(urlPathMatching(String.format("/cases/(.*)", case_details_dto.getId())))
            .withQueryParam("iac", equalTo("true"))
            .withQueryParam("caseevents", equalTo("true"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withBody(case_details)));

    return case_details_dto;
  }

  private void createCaseEventMock(UUID caseId) throws IOException {

    String f =
        loadResourceAsString(ActionEndpointIT.class, "ActionEndpointIT.CreatedCaseEvent.json");
    String case_event_created = String.format(f, caseId);

    this.wireMockRule.stubFor(
        post(urlPathMatching("/cases/(.*)/events"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
                    .withBody(case_event_created)));
  }

  private SampleAttributesDTO createSampleAttributesMock(UUID sampleUnitId) throws IOException {

    String f =
        loadResourceAsString(ActionEndpointIT.class, "ActionEndpointIT.SampleAttributes.json");
    String sample_attributes = String.format(f, sampleUnitId.toString());
    SampleAttributesDTO sample_attributes_dto =
        mapper.readValue(sample_attributes, SampleAttributesDTO.class);
    log.debug("sample_attributes to mock = " + sample_attributes_dto);

    this.wireMockRule.stubFor(
        get(urlPathMatching(String.format("/samples/%s/attributes", sampleUnitId)))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
                    .withBody(sample_attributes)));

    return sample_attributes_dto;
  }

  private void createSurveyDetailsMock() throws IOException {

    String survey_details =
        loadResourceAsString(ActionEndpointIT.class, "ActionEndpointIT.SurveyDetails.json");

    this.wireMockRule.stubFor(
        get(urlPathMatching("/surveys/(.*)"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
                    .withBody(survey_details)));
  }
}
