package uk.gov.ons.ctp.response.action.endpoint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.MvcHelper.putJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.action.endpoint.ActionRuleEndpoint.ACTION_PLAN_NOT_FOUND;
import static uk.gov.ons.ctp.response.action.endpoint.ActionRuleEndpoint.ACTION_RULE_NOT_FOUND;
import static uk.gov.ons.ctp.response.action.endpoint.ActionRuleEndpoint.ACTION_TYPE_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.*;
import ma.glasnost.orika.MapperFacade;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.representation.ActionRulePostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRulePutRequestDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.service.ActionRuleService;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;

/** ActionEndpoint Unit tests */
public final class ActionRuleEndpointUnitTest {

  private static final UUID ACTION_RULE_ID_1 =
      UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d78a");
  private static final UUID ACTION_RULE_ID_2 =
      UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d78b");
  private static final UUID ACTION_RULE_ID_3 =
      UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d78c");
  private static final UUID NON_EXISTING_ID =
      UUID.fromString("a0b9fe16-4e08-11e8-9c2d-fa7ae01bbebc");
  private static final UUID ACTION_PLAN_ID_1 =
      UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d79a");
  private static final String ACTION_TYPE_NAME_1 = "BSNOT";
  @InjectMocks private ActionRuleEndpoint actionRuleEndpoint;
  @Mock private ActionRuleService actionRuleService;
  @Mock private ActionPlanService actionPlanService;
  @Mock private ActionTypeService actionTypeService;
  @Spy private MapperFacade mapperFacade = new ActionBeanMapper();
  private ObjectMapper objectMapper = new CustomObjectMapper();
  private MockMvc mockMvc;
  private List<ActionPlan> actionPlans;
  private List<ActionType> actionTypes;

  /**
   * Initialises Mockito and loads Class Fixtures
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(actionRuleEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
  }

  /**
   * Test requesting ActionRules by action plan id but action plan does not exist.
   *
   * @throws Exception when getJson does
   */
  @Test
  public void findActionRulesByActionPlanNotFound() throws Exception {
    when(actionPlanService.findActionPlanById(ACTION_PLAN_ID_1)).thenReturn(null);

    final ResultActions resultActions =
        mockMvc.perform(getJson("/actionrules/actionplan/" + ACTION_PLAN_ID_1.toString()));

    resultActions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("findActionRulesByActionPlanId"));
  }

  /**
   * Test requesting ActionRules by action plan id but none found.
   *
   * @throws Exception when getJson does
   */
  @Test
  public void findActionRulesByActionPlanNoRules() throws Exception {
    when(actionPlanService.findActionPlanById(ACTION_PLAN_ID_1)).thenReturn(actionPlans.get(0));
    when(actionRuleService.findActionRulesByActionPlanFK(actionPlans.get(0).getActionPlanPK()))
        .thenReturn(Collections.emptyList());

    final ResultActions resultActions =
        mockMvc.perform(getJson("/actionrules/actionplan/" + ACTION_PLAN_ID_1.toString()));

    resultActions
        .andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("findActionRulesByActionPlanId"))
        .andExpect(jsonPath("$", Matchers.hasSize(0)));
  }

  /**
   * Test requesting ActionRules by action plan id and returning all the ones found.
   *
   * @throws Exception when getJson does
   */
  @Test
  public void findActionRulesByActionPlan() throws Exception {
    final List<ActionRule> results = new ArrayList<>();
    results.add(ActionRule.builder().id(ACTION_RULE_ID_1).priority(1).actionTypeFK(1).build());
    results.add(ActionRule.builder().id(ACTION_RULE_ID_2).priority(2).actionTypeFK(2).build());
    results.add(ActionRule.builder().id(ACTION_RULE_ID_3).priority(3).actionTypeFK(4).build());

    when(actionPlanService.findActionPlanById(ACTION_PLAN_ID_1)).thenReturn(actionPlans.get(0));
    when(actionRuleService.findActionRulesByActionPlanFK(actionPlans.get(0).getActionPlanPK()))
        .thenReturn(results);
    when(actionTypeService.findActionType(any(Integer.class))).thenReturn(actionTypes.get(0));

    final ResultActions resultActions =
        mockMvc.perform(getJson("/actionrules/actionplan/" + ACTION_PLAN_ID_1.toString()));

    resultActions
        .andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("findActionRulesByActionPlanId"))
        .andExpect(jsonPath("$", Matchers.hasSize(3)))
        .andExpect(
            jsonPath(
                "$[*].id",
                containsInAnyOrder(
                    ACTION_RULE_ID_1.toString(),
                    ACTION_RULE_ID_2.toString(),
                    ACTION_RULE_ID_3.toString())))
        .andExpect(jsonPath("$[*].priority", containsInAnyOrder(1, 2, 3)))
        .andExpect(
            jsonPath(
                "$[*].actionTypeName",
                containsInAnyOrder(
                    actionTypes.get(0).getName(),
                    actionTypes.get(0).getName(),
                    actionTypes.get(0).getName())));
  }

  /**
   * Test creating an Action rule with valid JSON.
   *
   * @throws Exception when postJson does
   */
  @Test
  public void createActionRuleGoodJsonProvided() throws Exception {
    ActionRule actionRule =
        new ActionRule(
            ACTION_RULE_ID_3,
            3,
            actionPlans.get(0).getActionPlanPK(),
            actionTypes.get(0).getActionTypePK(),
            "BSREM+0",
            "TEST description three",
            OffsetDateTime.now(),
            3);

    when(actionRuleService.createActionRule(any(ActionRule.class))).thenReturn(actionRule);
    when(actionPlanService.findActionPlanById(any(UUID.class))).thenReturn(actionPlans.get(0));
    when(actionTypeService.findActionTypeByName(ACTION_TYPE_NAME_1)).thenReturn(actionTypes.get(0));
    ActionRulePostRequestDTO actionRulePostRequestDTO =
        createActionRulePostRequestDTO(ACTION_PLAN_ID_1, ACTION_TYPE_NAME_1);

    final ResultActions resultActions =
        mockMvc.perform(
            postJson("/actionrules", objectMapper.writeValueAsString(actionRulePostRequestDTO)));

    resultActions
        .andExpect(status().isCreated())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("createActionRule"))
        .andExpect(jsonPath("$.*", Matchers.hasSize(6)))
        .andExpect(jsonPath("$.actionTypeName", is(ACTION_TYPE_NAME_1)));

    verify(actionTypeService, times(1)).findActionTypeByName(any(String.class));
    verify(actionPlanService, times(1)).findActionPlanById(any(UUID.class));
    verify(actionRuleService, times(1)).createActionRule(any(ActionRule.class));
  }

  /**
   * Test creating an Action rule with bad JSON.
   *
   * @throws Exception when postJson does
   */
  @Test
  public void createActionRuleBadJsonProvided() throws Exception {

    final ResultActions resultActions = mockMvc.perform(postJson("/actionrules", "{}"));

    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("createActionRule"));
  }

  /**
   * Test creating an Action rule with valid JSON but action plan does not exist .
   *
   * @throws Exception when postJson does
   */
  @Test
  public void createActionRuleActionPlanNotFound() throws Exception {
    when(actionPlanService.findActionPlanById(any(UUID.class))).thenReturn(null);
    ActionRulePostRequestDTO actionRulePostRequestDTO =
        createActionRulePostRequestDTO(ACTION_PLAN_ID_1, ACTION_TYPE_NAME_1);

    final ResultActions resultActions =
        mockMvc.perform(
            postJson("/actionrules", objectMapper.writeValueAsString(actionRulePostRequestDTO)));

    resultActions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("createActionRule"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(
            jsonPath(
                "$.error.message", is(String.format(ACTION_PLAN_NOT_FOUND, ACTION_PLAN_ID_1))));

    verify(actionPlanService, times(1)).findActionPlanById(any(UUID.class));
  }

  /**
   * Test creating an Action rule with valid JSON but action type does not exist .
   *
   * @throws Exception when postJson does
   */
  @Test
  public void createActionRuleActionType() throws Exception {
    when(actionPlanService.findActionPlanById(any(UUID.class))).thenReturn(actionPlans.get(0));
    when(actionTypeService.findActionTypeByName(ACTION_TYPE_NAME_1)).thenReturn(null);
    ActionRulePostRequestDTO actionRulePostRequestDTO =
        createActionRulePostRequestDTO(ACTION_PLAN_ID_1, ACTION_TYPE_NAME_1);

    final ResultActions resultActions =
        mockMvc.perform(
            postJson("/actionrules", objectMapper.writeValueAsString(actionRulePostRequestDTO)));

    resultActions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("createActionRule"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(
            jsonPath(
                "$.error.message", is(String.format(ACTION_TYPE_NOT_FOUND, ACTION_TYPE_NAME_1))));

    verify(actionTypeService, times(1)).findActionTypeByName(any(String.class));
    verify(actionPlanService, times(1)).findActionPlanById(any(UUID.class));
  }

  /**
   * Test updating action rule not found
   *
   * @throws Exception when putJson does
   */
  @Test
  public void updateActionRuleByActionRuleIdNotFound() throws Exception {
    ActionRulePutRequestDTO actionRulePutRequestDTO = createActionRulePutRequestDTO();
    final ResultActions resultActions =
        mockMvc.perform(
            putJson(
                String.format("/actionrules/%s", NON_EXISTING_ID),
                objectMapper.writeValueAsString(actionRulePutRequestDTO)));

    resultActions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("updateActionRule"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(
            jsonPath("$.error.message", is(String.format(ACTION_RULE_NOT_FOUND, NON_EXISTING_ID))));
  }

  /**
   * Test updating action
   *
   * @throws Exception when putJson does
   */
  @Test
  public void updateActionByActionId() throws Exception {
    ActionRule actionRule =
        new ActionRule(
            ACTION_RULE_ID_1,
            1,
            actionPlans.get(0).getActionPlanPK(),
            actionTypes.get(0).getActionTypePK(),
            "BSNOT+0",
            "TEST description one",
            OffsetDateTime.now(),
            1);

    when(actionRuleService.updateActionRule(any(ActionRule.class))).thenReturn(actionRule);
    when(actionTypeService.findActionType(any(Integer.class))).thenReturn(actionTypes.get(0));

    ActionRulePutRequestDTO actionRulePutRequestDTO = createActionRulePutRequestDTO();

    final ResultActions resultActions =
        mockMvc.perform(
            putJson(
                String.format("/actionrules/%s", ACTION_RULE_ID_1),
                objectMapper.writeValueAsString(actionRulePutRequestDTO)));

    resultActions
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("updateActionRule"))
        .andExpect(jsonPath("$.*", Matchers.hasSize(6)))
        .andExpect(jsonPath("$.id", is(ACTION_RULE_ID_1.toString())));
  }

  private ActionRulePostRequestDTO createActionRulePostRequestDTO(
    UUID actionPlanId, String actionTypeName) {
    ActionRulePostRequestDTO actionRulePostRequestDTO = new ActionRulePostRequestDTO();
    actionRulePostRequestDTO.setActionPlanId(actionPlanId);
    actionRulePostRequestDTO.setActionTypeName(actionTypeName);
    actionRulePostRequestDTO.setName("BSREM+45");
    actionRulePostRequestDTO.setDescription("Enrolment Reminder Letter(+45 days)");
    actionRulePostRequestDTO.setTriggerDateTime(OffsetDateTime.now());
    actionRulePostRequestDTO.setPriority(3);
    return actionRulePostRequestDTO;
  }

  private ActionRulePutRequestDTO createActionRulePutRequestDTO() {
    ActionRulePutRequestDTO actionRulePutRequestDTO = new ActionRulePutRequestDTO();
    actionRulePutRequestDTO.setName("BSREM+45");
    actionRulePutRequestDTO.setDescription("Enrolment Reminder Letter(+45 days)");
    actionRulePutRequestDTO.setTriggerDateTime(OffsetDateTime.now());
    actionRulePutRequestDTO.setPriority(3);
    return actionRulePutRequestDTO;
  }
}
