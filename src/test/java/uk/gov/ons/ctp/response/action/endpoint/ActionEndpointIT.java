package uk.gov.ons.ctp.response.action.endpoint;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Java6Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.*;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageBase;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

/** Integration tests for action endpoints */
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionEndpointIT {
  private static final Logger log = LoggerFactory.getLogger(ActionEndpointIT.class);

  @Autowired private ResourceLoader resourceLoader;

  @LocalServerPort private int port;

  @Autowired private ObjectMapper mapper;

  @Autowired private RabbitAdmin rabbitAdmin;

  @Autowired private AppConfig appConfig;

  @Rule public WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

  private Mapzer mapzer;

  private UUID sampleUnitId;
  private SampleAttributesDTO sampleAttributes;

  @Before
  public void setup() throws Exception {
    rabbitAdmin.purgeQueue("Action.Field", false);
    rabbitAdmin.purgeQueue("Action.Printer", false);
    mapzer = new Mapzer(resourceLoader);
    UnirestInitialiser.initialise(mapper);

    sampleUnitId = UUID.randomUUID();
    sampleAttributes = createSampleAttributesMock(sampleUnitId);
  }

  @Test
  public void testIncompleteCasesAreSentToField() throws Exception {
    UUID collexId = UUID.randomUUID();

    ActionPlanDTO actionPlan = createActionPlan();
    createActionRule(actionPlan.getId(), ActionType.SOCIALICF);

    // Create mocks
    createCollectionExerciseMock(collexId);
    CaseDetailsDTO case_details_dto =
        createCaseDetailsMock(UUID.randomUUID(), collexId, actionPlan.getId());
    createSurveyDetailsMock();
    createCaseEventMock(case_details_dto.getId());

    SimpleMessageSender sender = getMessageSender();
    SimpleMessageListener listener = getMessageListener();

    BlockingQueue<String> queue =
        listener.listen(
            SimpleMessageBase.ExchangeType.Direct,
            "action-outbound-exchange",
            "Action.CaseNotificationHandled.binding");

    String xml = getCaseNotificationXml(sampleUnitId, case_details_dto, collexId);
    sender.sendMessageToQueue("Case.LifecycleEvents", xml);

    String message = queue.take();
    assertThat(message).isNotNull();

    createAction(case_details_dto, ActionType.SOCIALICF);

    BlockingQueue<String> queue2 =
        listener.listen(
            SimpleMessageBase.ExchangeType.Direct,
            "action-outbound-exchange",
            "Action.Field.binding");

    String messageToField = queue2.take();
    assertThat(messageToField).isNotNull();

    ActionInstruction actionInstruction = getActionInstructionFromXml(messageToField);
    ActionAddress address = actionInstruction.getActionRequest().getAddress();

    checkAttributes(address);
  }

  @Test
  public void testAddressPopulatedInActionRequest() throws Exception {
    UUID collexId = UUID.randomUUID();

    ActionPlanDTO actionPlan = createActionPlan();

    // Create mocks
    createCollectionExerciseMock(collexId);
    CaseDetailsDTO case_details_dto =
        createCaseDetailsMock(UUID.randomUUID(), collexId, actionPlan.getId());
    createSurveyDetailsMock();
    createCaseEventMock(case_details_dto.getId());

    SimpleMessageSender sender = getMessageSender();
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

    String xml = getCaseNotificationXml(sampleUnitId, case_details_dto, collexId);
    sender.sendMessageToQueue("Case.LifecycleEvents", xml);

    String message = queue.take();
    assertThat(message).isNotNull();

    createAction(case_details_dto, ActionType.SOCIALNOT);

    String printer_message = queue2.take();
    assertThat(printer_message).isNotNull();

    log.debug("printer_message = " + printer_message);
    ActionInstruction actionInstruction = getActionInstructionFromXml(printer_message);
    ActionRequest actionRequest = actionInstruction.getActionRequest();
    ActionAddress address = actionRequest.getAddress();

    checkAttributes(address);
  }

  private void checkAttributes(ActionAddress address) {
    assertThat(address.getSampleUnitRef())
        .isEqualTo(
            sampleAttributes.getAttributes().get("TLA")
                + sampleAttributes.getAttributes().get("REFERENCE"));
    assertThat(address.getLine1()).isEqualTo(sampleAttributes.getAttributes().get("ADDRESS_LINE1"));
    assertThat(address.getPostcode()).isEqualTo(sampleAttributes.getAttributes().get("POSTCODE"));
    assertThat(address.getTownName()).isEqualTo(sampleAttributes.getAttributes().get("TOWN_NAME"));
  }

  private ActionInstruction getActionInstructionFromXml(String xml) throws JAXBException {
    JAXBContext xmlToObject = JAXBContext.newInstance(ActionInstruction.class);

    return (ActionInstruction)
        xmlToObject.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes()));
  }

  private void createAction(CaseDetailsDTO caseDetails, ActionType actionType)
      throws UnirestException {
    ActionPostRequestDTO apord = new ActionPostRequestDTO();
    apord.setCaseId(UUID.fromString(caseDetails.getId().toString()));
    apord.setCreatedBy("EMBRYO");
    apord.setActionTypeName(actionType.toString());
    apord.setPriority(1);

    Unirest.post("http://localhost:" + this.port + "/actions")
        .basicAuth("admin", "secret")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(apord)
        .asObject(ActionPostRequestDTO.class);
  }

  public void createActionRule(UUID actionPlanId, ActionType actionType) throws UnirestException {
    ActionRulePostRequestDTO apord = new ActionRulePostRequestDTO();
    apord.setActionPlanId(actionPlanId);
    apord.setActionTypeName(actionType);
    apord.setName(actionType.toString() + new Random().nextInt(365));
    apord.setDescription("I don't care what you are");
    apord.setPriority(3);
    apord.setTriggerDateTime(OffsetDateTime.now());

    Unirest.post("http://localhost:" + this.port + "/actionrules")
        .basicAuth("admin", "secret")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(apord)
        .asObject(ActionRulePostRequestDTO.class);
  }

  private String getCaseNotificationXml(
      UUID sampleUnitId, CaseDetailsDTO caseDetails, UUID collexId) throws Exception {
    CaseNotification casenot = new CaseNotification();
    casenot.setSampleUnitId(sampleUnitId.toString());
    casenot.setCaseId(caseDetails.getId().toString());
    casenot.setActionPlanId(caseDetails.getActionPlanId().toString());
    casenot.setExerciseId(collexId.toString());
    casenot.setNotificationType(NotificationType.ACTIVATED);
    casenot.setSampleUnitType("H");

    JAXBContext jaxbContext = JAXBContext.newInstance(CaseNotification.class);
    String xml =
        mapzer.convertObjectToXml(
            jaxbContext, casenot, "casesvc/xsd/outbound/caseNotification.xsd");

    return xml;
  }

  private ActionPlanDTO createActionPlan() throws UnirestException {
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

    return createActionPlanRes.getBody();
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

  private CaseDetailsDTO createCaseDetailsMock(UUID caseId, UUID collexId, UUID actionPlanId)
      throws IOException {
    String f = loadResourceAsString(ActionEndpointIT.class, "ActionEndpointIT.CaseDetailsDTO.json");
    String case_details = String.format(f, caseId, actionPlanId, collexId);
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
        get(urlPathMatching("/samples/(.*)/attributes"))
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
