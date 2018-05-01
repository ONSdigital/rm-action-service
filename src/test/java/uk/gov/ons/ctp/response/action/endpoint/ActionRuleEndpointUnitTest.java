package uk.gov.ons.ctp.response.action.endpoint;

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
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.service.ActionRuleService;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.ons.ctp.common.MvcHelper.*;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

/**
 * ActionEndpoint Unit tests
 */
public final class ActionRuleEndpointUnitTest {

  @InjectMocks
  private ActionRuleEndpoint actionRuleEndpoint;

  @Mock
  private ActionRuleService actionRuleService;

  @Mock
  private ActionPlanService actionPlanService;

  @Mock
  private ActionTypeService actionTypeService;

  @Spy
  private MapperFacade mapperFacade = new ActionBeanMapper();

  private MockMvc mockMvc;
  private List<ActionRule> actionRules;
  private List<ActionPlan> actionPlans;
  private List<ActionType> actionTypes;


  private static final UUID ACTION_RULE_ID_1 = UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d78a");
  private static final UUID ACTION_RULE_ID_2 = UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d78b");
  private static final UUID ACTION_RULE_ID_3 = UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d78c");

  private static final UUID ACTION_ID_3 = UUID.fromString("d24b3f17-bbf8-4c71-b2f0-a4334125d79a");



  /**
   * Initialises Mockito and loads Class Fixtures
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(actionRuleEndpoint)
        .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
        .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
        .build();

    actionRules = FixtureHelper.loadClassFixtures(ActionRule[].class);
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
    when(actionPlanService.findActionPlanById(ACTION_ID_3)).thenReturn(null);

    final ResultActions resultActions = mockMvc.perform(getJson(String.format("/actionrules/actionplan/" + ACTION_ID_3.toString())));

    resultActions.andExpect(status().isNotFound())
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
    when(actionRuleService.findActionRulesByActionPlanId(ACTION_ID_3)).thenReturn(Collections.emptyList());
    when(actionPlanService.findActionPlanById(ACTION_ID_3)).thenReturn(actionPlans.get(0));

    final ResultActions resultActions = mockMvc.perform(getJson(String.format("/actionrules/actionplan/" + ACTION_ID_3.toString())));

    resultActions.andExpect(status().is2xxSuccessful())
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
    for (int i = 0; i < 3; i++) {
      results.add((actionRules.get(i)));
    }
    when(actionRuleService.findActionRulesByActionPlanId(ACTION_ID_3)).thenReturn(results);
    when(actionPlanService.findActionPlanById(ACTION_ID_3)).thenReturn(actionPlans.get(0));
    when(actionTypeService.findActionType(any(Integer.class))).thenReturn(actionTypes.get(0));

    final ResultActions resultActions = mockMvc.perform(getJson(String.format("/actionrules/actionplan/" + ACTION_ID_3.toString())));

    resultActions.andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(ActionRuleEndpoint.class))
        .andExpect(handler().methodName("findActionRulesByActionPlanId"))
        .andExpect(jsonPath("$", Matchers.hasSize(3)))
        .andExpect(jsonPath("$[*].id", containsInAnyOrder(ACTION_RULE_ID_1.toString(), ACTION_RULE_ID_2.toString(),
            ACTION_RULE_ID_3.toString())))
        .andExpect(jsonPath("$[*].priority", containsInAnyOrder(1, 2, 3)))
        .andExpect(jsonPath("$[*].actionTypeName", containsInAnyOrder(actionTypes.get(0).getName(),
                actionTypes.get(0).getName(), actionTypes.get(0).getName())));
  }
}
