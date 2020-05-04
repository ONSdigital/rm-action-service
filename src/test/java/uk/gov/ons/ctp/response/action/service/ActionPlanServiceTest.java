package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;

/** Tests for the ActionPlanJobServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanServiceTest {

  @Mock private ActionPlanRepository actionPlanRepo;
  @InjectMocks private ActionPlanService actionPlanService;
  @Spy private MapperFacade mapperFacade = new ActionBeanMapper();

  private static final UUID ACTION_PLAN_ID =
      UUID.fromString("e71002ac-3575-47eb-b87f-cd9db92bf9a7");
  private static final String UPDATED_DESCRIPTION = "New description";
  private static final Date UPDATED_DATE = new Date();
  private static final Timestamp UPDATED_TIMESTAMP = new Timestamp(UPDATED_DATE.getTime());
  private static final String SELECTOR_KEY = "selectorKey";
  private static final String SELECTOR_VALUE = "selectorValue";

  private List<ActionPlan> actionPlans;
  private ActionPlan actionPlan;
  private HashMap<String, String> selectors;

  /** Before the test */
  @Before
  public void setUp() throws Exception {
    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    actionPlan = actionPlans.get(0);
    selectors = new HashMap<>();
    selectors.put(SELECTOR_KEY, SELECTOR_VALUE);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCreateActionPlan() {
    // Given
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(actionPlan);

    // When
    ActionPlan createdActionPlan = actionPlanService.createActionPlan(actionPlan);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(createdActionPlan);
    assertEquals(createdActionPlan.getName(), actionPlan.getName());
    assertEquals(createdActionPlan.getDescription(), actionPlan.getDescription());
  }

  @Test
  public void testUpdateActionPlanDescription() {
    // Given
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlan savedActionPlan = mapperFacade.map(actionPlan, ActionPlan.class);
    savedActionPlan.setDescription(UPDATED_DESCRIPTION);
    when(actionPlanRepo.saveAndFlush(savedActionPlan)).thenReturn(savedActionPlan);

    // When
    ActionPlan actionPlanUpdate = new ActionPlan();
    actionPlanUpdate.setDescription(UPDATED_DESCRIPTION);
    ActionPlan updatedActionPlan =
        actionPlanService.updateActionPlan(ACTION_PLAN_ID, actionPlanUpdate);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(savedActionPlan);
    assertEquals(updatedActionPlan.getDescription(), UPDATED_DESCRIPTION);
  }

  @Test
  public void testUpdateActionPlanRunTime() {
    // Given
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlan savedActionPlan = mapperFacade.map(actionPlan, ActionPlan.class);
    savedActionPlan.setLastRunDateTime(UPDATED_TIMESTAMP);
    when(actionPlanRepo.saveAndFlush(savedActionPlan)).thenReturn(savedActionPlan);

    // When
    ActionPlan actionPlanUpdate = new ActionPlan();
    actionPlanUpdate.setLastRunDateTime(UPDATED_TIMESTAMP);
    ActionPlan updatedActionPlan =
        actionPlanService.updateActionPlan(ACTION_PLAN_ID, actionPlanUpdate);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(savedActionPlan);
    assertEquals(updatedActionPlan.getLastRunDateTime(), UPDATED_TIMESTAMP);
  }

  @Test
  public void testUpdateActionPlanSelectors() {
    // Given
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlan savedActionPlan = mapperFacade.map(actionPlan, ActionPlan.class);
    savedActionPlan.setSelectors(selectors);
    when(actionPlanRepo.saveAndFlush(savedActionPlan)).thenReturn(savedActionPlan);

    // When
    ActionPlan actionPlanUpdate = new ActionPlan();
    actionPlanUpdate.setSelectors(selectors);
    ActionPlan updatedActionPlan =
        actionPlanService.updateActionPlan(ACTION_PLAN_ID, actionPlanUpdate);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(savedActionPlan);
    assertEquals(updatedActionPlan.getSelectors(), actionPlan.getSelectors());
  }
}
