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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.MvcHelper.putJson;
import static uk.gov.ons.ctp.common.error.RestExceptionHandler.PROVIDED_JSON_INCORRECT;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.action.service.impl.ActionPlanJobServiceImpl.CREATED_BY_SYSTEM;

/**
 * Unit tests for ActionPlan endpoint
 */
public class ActionPlanEndpointUnitTest {

  private static final Integer ACTIONPLANPK = 1;
  private static final Integer NON_EXISTING_ACTIONPLANID = 998;
  private static final Integer UNCHECKED_EXCEPTION = 999;

  private static final UUID ACTION_PLAN_1_ID = UUID.fromString("e71002ac-3575-47eb-b87f-cd9db92bf9a7");
  private static final UUID ACTION_PLAN_2_ID = UUID.fromString("0009e978-0932-463b-a2a1-b45cb3ffcb2a");
  private static final UUID SURVEY_ID = UUID.fromString("0009e978-0932-463b-a2a1-b45cb3ffcb2B");

  private static final String ACTION_PLAN_1_NAME = "C1O331D10E";
  private static final String ACTION_PLAN_2_NAME = "C1O331D10F";
  private static final String ACTION_PLAN_1_DESC = "Component 1 - England/online/field day ten/three reminders";
  private static final String ACTION_PLAN_2_DESC = "Component 2 - England/online/field day ten/three reminders";
  private static final String ACTION_PLAN_1_LAST_RUN_DATE_TIME = "2016-04-15T16:03:26.544+0100";
  private static final String ACTION_PLAN_2_LAST_RUN_DATE_TIME = "2016-04-15T16:03:26.644+0100";
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  private static final String ACTIONPLAN_JSON = "{\"id\":\"e71002ac-3575-47eb-b87f-cd9db92bf9a7\",\"name\":\"HH\", "
          + "\"description\":\"philippetesting\", \"createdBy\":\"SYSTEM\", \"lastGoodRunDateTime\":null}";
  private static final String ACTIONPLAN_INVALIDJSON = "{\"some\":\"joke\"}";

  private static final Timestamp ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP = Timestamp
          .valueOf("2016-03-09 11:15:48.023286");

  @InjectMocks
  private ActionPlanEndpoint actionPlanEndpoint;

  @Mock
  private ActionPlanService actionPlanService;

  private MockMvc mockMvc;

  @Spy
  private MapperFacade mapperFacade = new ActionBeanMapper();

  private List<ActionPlan> actionPlans;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(actionPlanEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    actionPlans =  FixtureHelper.loadClassFixtures(ActionPlan[].class);
  }

  /**
   * A Test to retrieve all action plans BUT none found
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlansNoneFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson("/actionplans"));

    actions.andExpect(status().isNoContent())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("findActionPlans"));
  }

  /**
   * A Test to retrieve all action plans BUT exception thrown
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlansUnCheckedException() throws Exception {
    when(actionPlanService.findActionPlans()).thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions = mockMvc.perform(getJson("/actionplans"));

    actions.andExpect(status().is5xxServerError())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("findActionPlans"))
            .andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())))
            .andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)))
            .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A Test to retrieve all action plans
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlansFound() throws Exception {
    when(actionPlanService.findActionPlans()).thenReturn(actionPlans);

    ResultActions actions = mockMvc.perform(getJson("/actionplans"));

    actions.andExpect(status().isOk())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("findActionPlans"))
            .andExpect(jsonPath("$", Matchers.hasSize(2)))
            .andExpect(jsonPath("$[0].*", hasSize(6)))
            .andExpect(jsonPath("$[1].*", hasSize(6)))
            .andExpect(jsonPath("$[*].id", containsInAnyOrder(ACTION_PLAN_1_ID.toString(),
                    ACTION_PLAN_2_ID.toString())))
// TODO           .andExpect(jsonPath("$[*].surveyId", containsInAnyOrder(SURVEY_ID.toString(),
// TODO                   SURVEY_ID.toString())))
            .andExpect(jsonPath("$[*].name", containsInAnyOrder(ACTION_PLAN_1_NAME, ACTION_PLAN_2_NAME)))
            .andExpect(jsonPath("$[*].description", containsInAnyOrder(ACTION_PLAN_1_DESC, ACTION_PLAN_2_DESC)))
            .andExpect(jsonPath("$[*].createdBy", containsInAnyOrder(CREATED_BY_SYSTEM, CREATED_BY_SYSTEM)))
            .andExpect(jsonPath("$[*].lastGoodRunDateTime", containsInAnyOrder(ACTION_PLAN_1_LAST_RUN_DATE_TIME,
                    ACTION_PLAN_2_LAST_RUN_DATE_TIME)));
  }

//  /**
//   * A Test
//   * @throws Exception exception thrown
//   */
//  @Test
//  public void findActionPlanFound() throws Exception {
//    when(actionPlanService.findActionPlan(ACTIONPLANPK)).thenReturn(new ActionPlan(ACTIONPLANPK, ACTIONPLAN3_ID,
//            ACTIONPLAN3_NAME, ACTIONPLAN3_DESC, CREATED_BY,
//            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP));
//
//    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplans/%s", ACTIONPLANPK)));
//
//    actions.andExpect(status().isOk())
//            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
//            .andExpect(handler().methodName("findActionPlanByActionPlanId"))
//            .andExpect(jsonPath("$.id", is(ACTIONPLAN3_ID.toString())))
//            .andExpect(jsonPath("$.name", is(ACTIONPLAN3_NAME)))
//            .andExpect(jsonPath("$.description", is(ACTIONPLAN3_DESC)))
//            .andExpect(jsonPath("$.createdBy", is(CREATED_BY)))
//            .andExpect(jsonPath("$.lastGoodRunDateTime", is(LAST_RUN_DATE_TIME)));
//  }

  /**
   * A Test
   * @throws Exception exception thrown
   */
  @Test
  public void findActionPlanNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplans/%s", NON_EXISTING_ACTIONPLANID)));

    actions.andExpect(status().isNotFound())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("findActionPlanByActionPlanId"))
            .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
            .andExpect(jsonPath("$.error.message", isA(String.class)))
            .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A Test
   * @throws Exception exception thrown
   */
  @Test
  public void findActionPlanUnCheckedException() throws Exception {
    when(actionPlanService.findActionPlan(UNCHECKED_EXCEPTION))
            .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplans/%s", UNCHECKED_EXCEPTION)));

    actions.andExpect(status().is5xxServerError())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("findActionPlanByActionPlanId"))
            .andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())))
            .andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)))
            .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A Test
   * @throws Exception exception thrown
   */
  @Test
  public void updateActionPlanNegativeScenarioInvalidJsonProvided() throws Exception {
    ResultActions actions = mockMvc.perform(putJson(String.format("/actionplans/%s", ACTIONPLANPK),
            ACTIONPLAN_INVALIDJSON));

    actions.andExpect(status().isBadRequest())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("updateActionPlanByActionPlanId"))
            .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
            .andExpect(jsonPath("$.error.message", is(PROVIDED_JSON_INCORRECT)))
            .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

//  /**
//   * A Test
//   * @throws Exception exception thrown
//   */
//  @Test
//  public void updateActionPlanHappyScenario() throws Exception {
//    when(actionPlanService.updateActionPlan(any(Integer.class), any(ActionPlan.class))).thenReturn(
//            new ActionPlan(ACTIONPLANPK, ACTIONPLAN3_ID, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC, CREATED_BY,
//            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP));
//
//    ResultActions actions = mockMvc.perform(putJson(String.format("/actionplans/%s", ACTIONPLANPK), ACTIONPLAN_JSON));
//
//    actions.andExpect(status().isOk())
//            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
//            .andExpect(handler().methodName("updateActionPlanByActionPlanId"))
//            .andExpect(jsonPath("$.id", is(ACTIONPLAN3_ID.toString())))
//            .andExpect(jsonPath("$.name", is(ACTIONPLAN3_NAME)))
//            .andExpect(jsonPath("$.description", is(ACTIONPLAN3_DESC)))
//            .andExpect(jsonPath("$.createdBy", is(CREATED_BY)))
//            .andExpect(jsonPath("$.lastGoodRunDateTime", is(LAST_RUN_DATE_TIME)));
//  }
}
