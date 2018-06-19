package uk.gov.ons.ctp.response.action.endpoint;

import ma.glasnost.orika.MapperFacade;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
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
import uk.gov.ons.ctp.common.matcher.DateMatcher;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanSelector;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.MvcHelper.putJson;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.error.RestExceptionHandler.INVALID_JSON;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.action.endpoint.ActionPlanEndpoint.ACTION_PLAN_NOT_FOUND;
import static uk.gov.ons.ctp.response.action.service.impl.ActionPlanJobServiceImpl.CREATED_BY_SYSTEM;

/**
 * Unit tests for ActionPlan endpoint
 */
public class ActionPlanEndpointUnitTest {

  private static final UUID NON_EXISTING_ACTION_PLAN_ID = UUID.fromString("e71002ac-3575-47eb-b87f-cd9db92bf9a1");
  private static final UUID ACTION_PLAN_1_ID = UUID.fromString("e71002ac-3575-47eb-b87f-cd9db92bf9a7");
  private static final UUID ACTION_PLAN_2_ID = UUID.fromString("0009e978-0932-463b-a2a1-b45cb3ffcb2a");
  private static final UUID ACTION_PLAN_3_ID = UUID.fromString("0009e978-0932-a2a1-463b-b45cb3ffcb2a");

  private static final String ACTION_PLAN_1_NAME = "C1O331D10E";
  private static final String ACTION_PLAN_2_NAME = "C1O331D10F";
  private static final String ACTION_PLAN_3_NAME = "C1O331D10G";
  private static final String ACTION_PLAN_1_DESC = "Component 1 - England/online/field day ten/three reminders";
  private static final String ACTION_PLAN_2_DESC = "Component 2 - England/online/field day ten/three reminders";
  private static final String ACTION_PLAN_3_DESC = "Component 3 - England/online/field day ten/three reminders";
  private static final String ACTION_PLAN_1_LAST_RUN_DATE_TIME = "2016-04-15T16:03:26.544+01:00";
  private static final String ACTION_PLAN_2_LAST_RUN_DATE_TIME = "2016-04-15T16:03:26.644+01:00";
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  private static final String ACTION_PLAN_JSON = "{\"description\":\"testing\",\"lastRunDateTime\":null}";
  private static final String ACTION_PLAN_INCORRECT_JSON = "{\"some\":\"joke\"}";

  private static final String ACTION_PLAN_CREATE_VALID_JSON = "{ \"name\": \"" + ACTION_PLAN_3_NAME
          + "\" , \"description\": \"" + ACTION_PLAN_3_DESC + "\", \"createdBy\": \"" + CREATED_BY_SYSTEM + "\"}";

  @InjectMocks
  private ActionPlanEndpoint actionPlanEndpoint;

  @Mock
  private ActionPlanService actionPlanService;

  private MockMvc mockMvc;

  @Spy
  private MapperFacade mapperFacade = new ActionBeanMapper();

  private List<ActionPlan> actionPlans;

  /**
   * Initialises Mockito and loads Class Fixtures
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
        .standaloneSetup(actionPlanEndpoint)
        .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
        .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
        .build();

    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
  }

  /**
   * A Test to retrieve all action plans BUT none found
   *
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlansNoneFound() throws Exception {
    final ResultActions actions = mockMvc.perform(getJson("/actionplans"));

    actions.andExpect(status().isNoContent())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("findActionPlans"));
  }

  /**
   * A Test to retrieve all action plans BUT exception thrown
   *
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlansUnCheckedException() throws Exception {
    when(actionPlanService.findActionPlans()).thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    final ResultActions actions = mockMvc.perform(getJson("/actionplans"));

    actions.andExpect(status().is5xxServerError())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("findActionPlans"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())))
        .andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A Test to retrieve all action plans
   *
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlansFound() throws Exception {
    when(actionPlanService.findActionPlans()).thenReturn(actionPlans);

    final ResultActions actions = mockMvc.perform(getJson("/actionplans"));

    actions.andExpect(status().isOk())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("findActionPlans"))
        .andExpect(jsonPath("$", Matchers.hasSize(3)))
        .andExpect(jsonPath("$[0].*", hasSize(6)))
        .andExpect(jsonPath("$[1].*", hasSize(6)))
        .andExpect(jsonPath("$[*].id", containsInAnyOrder(ACTION_PLAN_1_ID.toString(),
            ACTION_PLAN_2_ID.toString(), ACTION_PLAN_3_ID.toString())))
        .andExpect(jsonPath("$[*].name", containsInAnyOrder(ACTION_PLAN_1_NAME, ACTION_PLAN_2_NAME,
                ACTION_PLAN_3_NAME)))
        .andExpect(jsonPath("$[*].description", containsInAnyOrder(ACTION_PLAN_1_DESC, ACTION_PLAN_2_DESC,
                ACTION_PLAN_3_DESC)))
        .andExpect(jsonPath("$[*].createdBy", containsInAnyOrder(CREATED_BY_SYSTEM, CREATED_BY_SYSTEM,
                CREATED_BY_SYSTEM)))
        .andExpect(jsonPath("$[*].lastRunDateTime", contains(new DateMatcher(ACTION_PLAN_1_LAST_RUN_DATE_TIME),
            new DateMatcher(ACTION_PLAN_2_LAST_RUN_DATE_TIME), IsNull.nullValue())));
  }

  /**
   * A Test to retrieve an ActionPlan BUT not found
   *
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlanNotFound() throws Exception {
    final ResultActions actions = mockMvc.perform(getJson(String.format("/actionplans/%s", NON_EXISTING_ACTION_PLAN_ID)));

    actions.andExpect(status().isNotFound())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("findActionPlanByActionPlanId"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(jsonPath("$.error.message", is(String.format(ACTION_PLAN_NOT_FOUND, NON_EXISTING_ACTION_PLAN_ID))))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A Test to retrieve an ActionPlan BUT exception thrown
   *
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlanUnCheckedException() throws Exception {
    when(actionPlanService.findActionPlanById(NON_EXISTING_ACTION_PLAN_ID)).thenThrow(
        new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    final ResultActions actions = mockMvc.perform(getJson(String.format("/actionplans/%s", NON_EXISTING_ACTION_PLAN_ID)));

    actions.andExpect(status().is5xxServerError())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("findActionPlanByActionPlanId"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())))
        .andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A Test to retrieve an ActionPlan
   *
   * @throws Exception exception thrown when getJson does
   */
  @Test
  public void findActionPlanFound() throws Exception {
    when(actionPlanService.findActionPlanById(ACTION_PLAN_1_ID)).thenReturn(actionPlans.get(0));

    final ResultActions actions = mockMvc.perform(getJson(String.format("/actionplans/%s", ACTION_PLAN_1_ID)));

    actions.andExpect(status().isOk())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("findActionPlanByActionPlanId"))
        .andExpect(jsonPath("$.*", hasSize(6)))
        .andExpect(jsonPath("$.id", is(ACTION_PLAN_1_ID.toString())))
        .andExpect(jsonPath("$.name", is(ACTION_PLAN_1_NAME)))
        .andExpect(jsonPath("$.description", is(ACTION_PLAN_1_DESC)))
        .andExpect(jsonPath("$.createdBy", is(CREATED_BY_SYSTEM)))
        .andExpect(jsonPath("$.lastRunDateTime", is(new DateMatcher(ACTION_PLAN_1_LAST_RUN_DATE_TIME))));

    System.out.println(actions.andReturn().getResponse().getContentAsString());

  }

  /**
   * A Test to update an ActionPlan with incorrect json
   *
   * @throws Exception exception thrown when putJson does
   */
/*  @Test
  public void updateActionPlanIncorrectJson1() throws Exception {
    ResultActions actions = mockMvc.perform(putJson(String.format("/actionplans/%s", ACTION_PLAN_1_ID),
        ACTION_PLAN_INCORRECT_JSON));

    actions.andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("updateActionPlanByActionPlanId"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
        .andExpect(jsonPath("$.error.message", is(PROVIDED_JSON_INCORRECT)))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }*/

  /**
   * A Test to update an ActionPlan with valid json
   *
   * @throws Exception exception thrown when putJson does
   */
  @Test
  public void updateActionPlan() throws Exception {
    when(actionPlanService.updateActionPlan(any(UUID.class), any(ActionPlan.class), any(ActionPlanSelector.class)))
            .thenReturn(actionPlans.get(0));

    final ResultActions actions = mockMvc
        .perform(putJson(String.format("/actionplans/%s", ACTION_PLAN_1_ID), ACTION_PLAN_JSON));

    actions.andExpect(status().isOk())
        .andExpect(handler().handlerType(ActionPlanEndpoint.class))
        .andExpect(handler().methodName("updateActionPlanByActionPlanId"))
        .andExpect(jsonPath("$.*", hasSize(6)))
        .andExpect(jsonPath("$.id", is(ACTION_PLAN_1_ID.toString())))
        .andExpect(jsonPath("$.name", is(ACTION_PLAN_1_NAME)))
        .andExpect(jsonPath("$.description", is(ACTION_PLAN_1_DESC)))
        .andExpect(jsonPath("$.createdBy", is(CREATED_BY_SYSTEM)))
        .andExpect(jsonPath("$.lastRunDateTime", is(new DateMatcher(ACTION_PLAN_1_LAST_RUN_DATE_TIME))));
  }


  /**
   * Test creating an Action plan
   *
   * @throws Exception when postJson does
   */
  @Test
  public void createActionPlan() throws Exception {
    when(actionPlanService.findActionPlanByName(any(String.class))).thenReturn(null);
    ActionPlanDTO actionPlanDTO = mapperFacade.map(actionPlans.get(2), ActionPlanDTO.class);
    when(actionPlanService.createActionPlan(any(ActionPlan.class), any(ActionPlanSelector.class)))
            .thenReturn(actionPlanDTO);

    final ResultActions resultActions = mockMvc.perform(postJson("/actionplans", ACTION_PLAN_CREATE_VALID_JSON));

    resultActions.andExpect(status().isCreated())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("createActionPlan"))
            .andExpect(jsonPath("$.*", Matchers.hasSize(6)))
            .andExpect(jsonPath("$.name", is(ACTION_PLAN_3_NAME)))
            .andExpect(jsonPath("$.description", is(ACTION_PLAN_3_DESC)))
            .andExpect(jsonPath("$.createdBy", is(CREATED_BY_SYSTEM)))
            .andExpect(jsonPath("$.lastRunDateTime", is(IsNull.nullValue())));

    verify(actionPlanService, times(1)).findActionPlanByName(any(String.class));
    verify(actionPlanService, times(1)).createActionPlan(any(ActionPlan.class),
                                                                                 any(ActionPlanSelector.class));
  }

  /**
   * Test creating an Action plan with valid JSON but name already exists.
   *
   * @throws Exception when postJson does
   */
  @Test
  public void createActionNameExists() throws Exception {
    when(actionPlanService.findActionPlanByName(any(String.class))).thenReturn(actionPlans.get(2));

    final ResultActions resultActions = mockMvc.perform(postJson("/actionplans", ACTION_PLAN_CREATE_VALID_JSON));

    resultActions.andExpect(status().isConflict())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("createActionPlan"))
            .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_VERSION_CONFLICT.name())))
            .andExpect(jsonPath("$.error.message",
                    is("Action plan with name " + actionPlans.get(2).getName() + " already exists")));

    verify(actionPlanService, times(1)).findActionPlanByName(any(String.class));
    verify(actionPlanService, never()).createActionPlan(any(), any());
  }


  /**
   * Test creating an Action plan with invalid JSON Property.
   * @throws Exception when postJson does
   */
  @Test
  public void createActionPlanInvalidJsonProvided() throws Exception {
    ResultActions resultActions = mockMvc.perform(postJson("/actionplans", ACTION_PLAN_INCORRECT_JSON));

    resultActions.andExpect(status().isBadRequest())
            .andExpect(handler().handlerType(ActionPlanEndpoint.class))
            .andExpect(handler().methodName("createActionPlan"))
            .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
            .andExpect(jsonPath("$.error.message", is(INVALID_JSON)));

    verify(actionPlanService, never()).createActionPlan(any(), any());
  }

}
