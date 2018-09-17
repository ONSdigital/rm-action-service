package uk.gov.ons.ctp.response.action.endpoint;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.transaction.Transactional;
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
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.action.domain.repository.*;
import uk.gov.ons.ctp.response.action.representation.*;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionRuleEndpointIT {
  private static final Logger log = LoggerFactory.getLogger(ActionRuleEndpointIT.class);

  @Autowired private ResourceLoader resourceLoader;

  @Autowired private ActionRepository actionRepository;

  @Autowired private ActionCaseRepository actionCaseRepository;

  @Autowired private ActionPlanRepository actionPlanRepository;

  @Autowired private ActionRuleRepository actionRuleRepository;

  @Autowired private ActionPlanJobRepository actionPlanJobRepository;

  @ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @LocalServerPort private int port;

  @Autowired private ObjectMapper mapper;

  private Mapzer mapzer;

  @Before
  @Transactional
  public void setup() {
    mapzer = new Mapzer(resourceLoader);
    UnirestInitialiser.initialise(mapper);
    actionCaseRepository.deleteAll();
    actionRepository.deleteAll();
    actionPlanJobRepository.deleteAll();
    actionRuleRepository.deleteAll();
    actionPlanRepository.deleteAll();
  }

  @Test
  public void createActionRule() throws UnirestException {
    // Given
    ActionPlanDTO actionPlanDTO = createActionPlan();

    ActionRulePostRequestDTO postRequestBody = createActionRulePostRequestDTO(actionPlanDTO);

    // When
    HttpResponse<ActionRuleDTO> response = postActionRule(postRequestBody);

    // Then
    assertEquals(201, response.getStatus());
  }

  @Test
  public void readCreatedActionRule() throws UnirestException {
    // Given
    ActionPlanDTO actionPlanDTO = createActionPlan();

    ActionRulePostRequestDTO postRequestBody = createActionRulePostRequestDTO(actionPlanDTO);

    HttpResponse<ActionRuleDTO> postResponse = postActionRule(postRequestBody);
    assertEquals(201, postResponse.getStatus());

    // When
    HttpResponse<ActionRuleDTO[]> getResponse = getActionRules(actionPlanDTO.getId());

    // Then
    assertEquals(200, getResponse.getStatus());
    assertEquals(1, getResponse.getBody().length);
    assertEquals(postRequestBody.getName(), getResponse.getBody()[0].getName());
    assertEquals(
        postRequestBody.getTriggerDateTime().toEpochSecond(),
        getResponse.getBody()[0].getTriggerDateTime().toEpochSecond());
  }

  @Test
  public void updateCreatedActionRule() throws UnirestException {
    // Given
    ActionPlanDTO actionPlanDTO = createActionPlan();

    ActionRulePostRequestDTO postRequestBody = createActionRulePostRequestDTO(actionPlanDTO);

    HttpResponse<ActionRuleDTO> postResponse = postActionRule(postRequestBody);
    assertEquals(201, postResponse.getStatus());

    OffsetDateTime editedTriggerDateTime = OffsetDateTime.now().plusDays(3);

    ActionRulePutRequestDTO putRequestBody = new ActionRulePutRequestDTO();
    putRequestBody.setTriggerDateTime(editedTriggerDateTime);
    putRequestBody.setDescription("Another description");
    putRequestBody.setName("AnotherName");

    // When
    HttpResponse<ActionRuleDTO> putResponse =
        putActionRule(putRequestBody, postResponse.getBody().getId());

    // Then
    assertEquals(200, putResponse.getStatus());

    HttpResponse<ActionRuleDTO[]> getResponse = getActionRules(actionPlanDTO.getId());
    assertEquals(200, getResponse.getStatus());
    assertEquals(1, getResponse.getBody().length);

    ActionRuleDTO updatedActionRule = getResponse.getBody()[0];
    assertEquals(putRequestBody.getName(), updatedActionRule.getName());
    assertEquals(
        putRequestBody.getTriggerDateTime().toEpochSecond(),
        updatedActionRule.getTriggerDateTime().toEpochSecond());
    assertEquals(putRequestBody.getDescription(), updatedActionRule.getDescription());
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

    assertEquals(201, response.getStatus());

    return response.getBody();
  }

  private HttpResponse<ActionRuleDTO> postActionRule(ActionRulePostRequestDTO postBody)
      throws UnirestException {
    return Unirest.post("http://localhost:" + this.port + "/actionrules")
        .basicAuth("admin", "secret")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(postBody)
        .asObject(ActionRuleDTO.class);
  }

  private HttpResponse<ActionRuleDTO[]> getActionRules(UUID actionPlanUUID)
      throws UnirestException {
    return Unirest.get(
            "http://localhost:"
                + this.port
                + "/actionrules/actionplan/"
                + actionPlanUUID.toString())
        .basicAuth("admin", "secret")
        .header("accept", "application/json")
        .asObject(ActionRuleDTO[].class);
  }

  private HttpResponse<ActionRuleDTO> putActionRule(
      ActionRulePutRequestDTO putBody, UUID actionRuleId) throws UnirestException {
    return Unirest.put("http://localhost:" + this.port + "/actionrules/" + actionRuleId.toString())
        .basicAuth("admin", "secret")
        .header("accept", "application/json")
        .header("Content-Type", "application/json")
        .body(putBody)
        .asObject(ActionRuleDTO.class);
  }

  private ActionRulePostRequestDTO createActionRulePostRequestDTO(ActionPlanDTO actionPlanDTO) {
    ActionRulePostRequestDTO actionRuleDto = new ActionRulePostRequestDTO();
    actionRuleDto.setTriggerDateTime(OffsetDateTime.now());
    actionRuleDto.setActionPlanId(actionPlanDTO.getId());
    actionRuleDto.setDescription("Notification file");
    actionRuleDto.setName("Notifaction");
    actionRuleDto.setActionTypeName(ActionTypes.BSNL);
    return actionRuleDto;
  }
}
