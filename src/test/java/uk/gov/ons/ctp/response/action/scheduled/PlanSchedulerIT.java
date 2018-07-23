package uk.gov.ons.ctp.response.action.scheduled;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
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
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRuleRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanPostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRuleDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRulePostRequestDTO;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.Attributes;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageBase.ExchangeType;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

/** Integration tests for creating action requests */
@Slf4j
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanSchedulerIT {

  @Autowired private ResourceLoader resourceLoader;

  @LocalServerPort private int port;

  @Autowired private AppConfig appConfig;

  @Rule public WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired private ActionCaseRepository actionCaseRepository;

  @Autowired private ActionRepository actionRepository;

  @Autowired private ActionPlanJobRepository actionPlanJobRepository;

  @Autowired private ActionPlanRepository actionPlanRepository;

  @Autowired private ActionRuleRepository actionRuleRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RabbitAdmin admin;

  private Mapzer mapzer;

  @Before
  @Transactional
  public void setup() {
    admin.purgeQueue("Case.LifecycleEvents", false);
    admin.purgeQueue("Action.Printer", false);
    wireMockRule.resetAll();
    mapzer = new Mapzer(resourceLoader);
    UnirestInitialiser.initialise(objectMapper);
    actionCaseRepository.deleteAllInBatch();
    actionRepository.deleteAllInBatch();
    actionPlanJobRepository.deleteAllInBatch();
    actionRuleRepository.deleteAllInBatch();
    actionPlanRepository.deleteAllInBatch();
  }

  private ActionPlanDTO createActionPlan() throws UnirestException {
    ActionPlanPostRequestDTO actionPlanDto = new ActionPlanPostRequestDTO();
    actionPlanDto.setName("notification2");
    actionPlanDto.setDescription("bres enrolment notification");
    actionPlanDto.setCreatedBy("TEST");

    HttpResponse<ActionPlanDTO> response =
        Unirest.post("http://localhost:" + this.port + "/actionplans")
            .basicAuth("admin", "secret")
            .header("accept", "application/json")
            .header("Content-Type", "application/json")
            .body(actionPlanDto)
            .asObject(ActionPlanDTO.class);

    return response.getBody();
  }

  private ActionRuleDTO createActionRule(
      ActionPlanDTO actionPlanDTO, OffsetDateTime triggerDateTime) throws UnirestException {
    ActionRulePostRequestDTO actionRuleDto = new ActionRulePostRequestDTO();
    actionRuleDto.setTriggerDateTime(triggerDateTime);
    actionRuleDto.setActionPlanId(actionPlanDTO.getId());
    actionRuleDto.setDescription("Notification file");
    actionRuleDto.setName("Notifaction");
    actionRuleDto.setActionTypeName("BSNL");
    actionRuleDto.setPriority(3);

    HttpResponse<ActionRuleDTO> response =
        Unirest.post("http://localhost:" + this.port + "/actionrules")
            .basicAuth("admin", "secret")
            .header("accept", "application/json")
            .header("Content-Type", "application/json")
            .body(actionRuleDto)
            .asObject(ActionRuleDTO.class);

    return response.getBody();
  }

  private void createActionCase(
      final UUID collectionExerciseId,
      final ActionPlanDTO actionPlan,
      final UUID partyId,
      final UUID caseId,
      final String sampleUnitType)
      throws Exception {
    final UUID sampleUnitId = UUID.fromString("af41a8a6-999c-4d40-9f97-e632184a179a");

    CaseNotification caseNotification = new CaseNotification();
    caseNotification.setSampleUnitId(sampleUnitId.toString());
    caseNotification.setCaseId(caseId.toString());
    caseNotification.setActionPlanId(actionPlan.getId().toString());
    caseNotification.setExerciseId(collectionExerciseId.toString());
    caseNotification.setNotificationType(NotificationType.ACTIVATED);
    caseNotification.setSampleUnitType(sampleUnitType);
    caseNotification.setPartyId(partyId.toString());

    JAXBContext jaxbContext = JAXBContext.newInstance(CaseNotification.class);
    String xml =
        mapzer.convertObjectToXml(
            jaxbContext, caseNotification, "casesvc/xsd/outbound/caseNotification.xsd");

    Rabbitmq config = appConfig.getRabbitmq();

    SimpleMessageSender sender =
        new SimpleMessageSender(
            config.getHost(), config.getPort(), config.getUsername(), config.getPassword());

    sender.sendMessageToQueue("Case.LifecycleEvents", xml);
  }

  private String pollForPrinterAction() throws InterruptedException {
    Rabbitmq config = appConfig.getRabbitmq();

    SimpleMessageListener listener =
        new SimpleMessageListener(
            config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
    BlockingQueue<String> queue =
        listener.listen(ExchangeType.Direct, "action-outbound-exchange", "Action.Printer.binding");
    int timeout = 4;
    return queue.poll(timeout, TimeUnit.SECONDS);
  }

  private void mockCaseDetailsMock(
      UUID collectionExerciseId, UUID actionPlanId, UUID partyId, UUID caseId) throws IOException {

    CaseGroupDTO caseGroupDTO = new CaseGroupDTO();
    caseGroupDTO.setCaseGroupStatus(CaseGroupStatus.INPROGRESS);
    caseGroupDTO.setCollectionExerciseId(collectionExerciseId);
    caseGroupDTO.setPartyId(partyId);

    CaseDetailsDTO caseDetailsDTO = new CaseDetailsDTO();
    caseDetailsDTO.setId(caseId);
    caseDetailsDTO.setCaseEvents(new ArrayList<>());
    caseDetailsDTO.setCaseGroup(caseGroupDTO);
    caseDetailsDTO.setSampleUnitType("B");
    caseDetailsDTO.setActionPlanId(actionPlanId);
    caseDetailsDTO.setPartyId(partyId);

    wireMockRule.stubFor(
        get(urlPathMatching(String.format("/cases/%s", caseDetailsDTO.getId())))
            .withQueryParam("iac", equalTo("true"))
            .withQueryParam("caseevents", equalTo("true"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(caseDetailsDTO))));
  }

  private void mockGetCollectionExercise(
      OffsetDateTime startDate, OffsetDateTime endDate, UUID surveyId, UUID collectionExerciseId)
      throws JsonProcessingException {

    CollectionExerciseDTO collectionExerciseDTO = new CollectionExerciseDTO();
    collectionExerciseDTO.setId(collectionExerciseId);
    collectionExerciseDTO.setScheduledStartDateTime(Date.from(startDate.toInstant()));
    collectionExerciseDTO.setScheduledEndDateTime(Date.from(endDate.toInstant()));
    collectionExerciseDTO.setSurveyId(surveyId.toString());

    wireMockRule.stubFor(
        get(urlPathEqualTo(
                String.format("/collectionexercises/%s", collectionExerciseId.toString())))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(collectionExerciseDTO))));
  }

  private void mockGetPartyWithAssociationsFilteredBySurvey(String sampleUnitType, UUID partyId)
      throws JsonProcessingException {
    PartyDTO partyDTO = new PartyDTO();
    partyDTO.setId(partyId.toString());
    partyDTO.setAssociations(new ArrayList<>());
    partyDTO.setAttributes(new Attributes());

    wireMockRule.stubFor(
        get(urlPathEqualTo(
                String.format(
                    "/party-api/v1/parties/type/%s/id/%s", sampleUnitType, partyId.toString())))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(partyDTO))));
  }

  private void mockSurveyDetails(UUID surveyId) throws IOException {
    SurveyDTO surveyDetailsDTO = new SurveyDTO();
    surveyDetailsDTO.setId(surveyId.toString());
    surveyDetailsDTO.setId("095a824e-a57d-44a0-bfeb-ae8d2bf62221");

    wireMockRule.stubFor(
        get(urlPathMatching("/surveys/(.*)"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(surveyDetailsDTO))));
  }

  private void mockGetCaseEvent() throws JsonProcessingException {

    final CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCategory(CategoryName.ACTION_CREATED);
    caseEventDTO.setCreatedBy("TEST");
    caseEventDTO.setDescription("Test description");

    wireMockRule.stubFor(
        post(urlPathMatching("/cases/(.*)/events"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
                    .withBody(objectMapper.writeValueAsString(caseEventDTO))));
  }

  @Test
  public void testNoActionsCreatedWhenActionPlanHasNotStarted() throws Exception {
    //// Given
    ActionPlanDTO actionPlan = createActionPlan();

    final UUID surveyId = UUID.fromString("2ae42f73-3324-4d6b-b071-d9ae0e1fbe22");
    final UUID partyId = UUID.fromString("2050428f-1cea-4e71-8b0c-f00d0d354a0f");

    UUID collectionExerciseId = UUID.fromString("09a47930-c3f1-470d-a4a3-f2f5454d8e99");
    OffsetDateTime startDate = OffsetDateTime.now().plusDays(1);
    OffsetDateTime endDate = OffsetDateTime.now().plusDays(3);
    mockGetCollectionExercise(startDate, endDate, surveyId, collectionExerciseId);

    OffsetDateTime triggerDateTime = OffsetDateTime.now().plusDays(2);
    createActionRule(actionPlan, triggerDateTime);

    UUID caseId = UUID.fromString("7d84ffcd-4a5d-4427-a712-581437ebd6c2");
    String sampleUnitType = "B";

    mockCaseDetailsMock(collectionExerciseId, actionPlan.getId(), partyId, caseId);
    createActionCase(collectionExerciseId, actionPlan, partyId, caseId, sampleUnitType);

    //// When PlanScheduler and ActionDistributor runs

    //// Then
    final String message = pollForPrinterAction();
    assertThat(message, nullValue());
  }

  @Test
  public void testNoActionsCreatedWhenActionPlanHasEnded() throws Exception {
    //// Given
    final ActionPlanDTO actionPlan = createActionPlan();

    final UUID partyId = UUID.fromString("cca5e7fc-9062-476d-94c5-5c46efd1ef54");
    final UUID surveyId = UUID.fromString("e0af7bd1-5ddf-4861-93a9-27d3eec31799");

    UUID collectionExcerciseId = UUID.fromString("7245ce02-139f-44d1-9d4e-f03ebdfcf0b1");
    OffsetDateTime startDate = OffsetDateTime.now().minusDays(3);
    OffsetDateTime endDate = OffsetDateTime.now().minusDays(1);
    mockGetCollectionExercise(startDate, endDate, surveyId, collectionExcerciseId);

    OffsetDateTime triggerDateTime = OffsetDateTime.now().minusDays(2);
    createActionRule(actionPlan, triggerDateTime);

    UUID caseId = UUID.fromString("61bcd60e-d91f-49db-a572-a2033b044baa");
    String sampleUnitType = "B";

    createActionCase(collectionExcerciseId, actionPlan, partyId, caseId, sampleUnitType);

    //// When PlanScheduler and ActionDistributor runs

    //// Then
    String message = pollForPrinterAction();
    assertThat(message, nullValue());
  }

  @Test
  public void testActiveActionPlanJobAndActionPlanCreatesAction() throws Exception {
    //// Given
    ActionPlanDTO actionPlan = createActionPlan();

    UUID surveyId = UUID.fromString("2e679bf1-18c9-4945-86f0-126d6c9aae4d");
    UUID partyId = UUID.fromString("905810f0-777f-48a1-ad79-3ef230551da1");

    UUID collectionExcerciseId = UUID.fromString("eea05d8a-f7ae-41de-ad9d-060acd024d38");
    OffsetDateTime startDate = OffsetDateTime.now().minusDays(3);
    OffsetDateTime endDate = OffsetDateTime.now().plusDays(2);
    mockGetCollectionExercise(startDate, endDate, surveyId, collectionExcerciseId);

    OffsetDateTime triggerDateTime = OffsetDateTime.now().minusDays(1);
    ActionRuleDTO actionRule = createActionRule(actionPlan, triggerDateTime);

    UUID caseId = UUID.fromString("b12aa9e7-4e6d-44aa-b7b5-4b507bbcf6c5");
    String sampleUnitType = "B";

    createActionCase(collectionExcerciseId, actionPlan, partyId, caseId, sampleUnitType);
    mockCaseDetailsMock(collectionExcerciseId, actionPlan.getId(), partyId, caseId);
    mockSurveyDetails(surveyId);
    mockGetPartyWithAssociationsFilteredBySurvey(sampleUnitType, partyId);
    mockGetCaseEvent();

    //// When PlanScheduler and ActionDistributor runs

    //// Then
    String message = pollForPrinterAction();
    assertThat(message, notNullValue());

    StringReader reader = new StringReader(message);
    JAXBContext xmlToObject = JAXBContext.newInstance(ActionInstruction.class);
    ActionInstruction actionInstruction =
        (ActionInstruction) xmlToObject.createUnmarshaller().unmarshal(reader);

    assertThat(caseId.toString(), is(actionInstruction.getActionRequest().getCaseId()));
    assertThat(
        actionPlan.getId().toString(), is(actionInstruction.getActionRequest().getActionPlan()));
    assertThat(
        actionRule.getActionTypeName(), is(actionInstruction.getActionRequest().getActionType()));

    assertThat(pollForPrinterAction(), nullValue());
  }
}
