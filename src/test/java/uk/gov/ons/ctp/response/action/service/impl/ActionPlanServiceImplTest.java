package uk.gov.ons.ctp.response.action.service.impl;

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
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;

/** Tests for the ActionPlanJobServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanServiceImplTest {

  @Mock private ActionPlanRepository actionPlanRepo;
  @InjectMocks private ActionPlanServiceImpl actionPlanServiceImpl;
  @Spy private MapperFacade mapperFacade = new ActionBeanMapper();

  private static final UUID ACTION_PLAN_ID =
      UUID.fromString("e71002ac-3575-47eb-b87f-cd9db92bf9a7");
  private static final String UPDATED_DESCRIPTION = "New description";
  private static final Date UPDATED_DATE = new Date();
  private static final Timestamp UPDATED_TIMESTAMP = new Timestamp(UPDATED_DATE.getTime());
  private static final String SELECTOR_KEY = "selectorKey";
  private static final String SELECTOR_VALUE = "selectorValue";

  private List<ActionPlan> actionPlans;
  private HashMap<String, String> selectors;

  /** Before the test */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    selectors = new HashMap<>();
    selectors.put(SELECTOR_KEY, SELECTOR_VALUE);
  }

  @Test
  public void testFindActionPlansBySelectors() {
    // Given
    when(this.actionPlanRepo.findBySelectorsIn(any())).thenReturn(actionPlans);

    // When
    ActionPlan actionPlan = actionPlans.get(0);
    List<ActionPlan> foundActionPlans =
        actionPlanServiceImpl.findActionPlansBySelectors(actionPlan.getSelectors());

    // Then
    assertEquals(foundActionPlans.get(0).getSelectors(), actionPlan.getSelectors());
  }

  @Test
  public void testCreateActionPlan() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(actionPlan);

    // When
    ActionPlan createdActionPlan = actionPlanServiceImpl.createActionPlan(actionPlan);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(createdActionPlan);
    assertEquals(createdActionPlan.getName(), createdActionPlan.getName());
    assertEquals(createdActionPlan.getDescription(), createdActionPlan.getDescription());
  }

  @Test
  public void testUpdateActionPlanDescription() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlan savedActionPlan = mapperFacade.map(actionPlan, ActionPlan.class);
    savedActionPlan.setDescription(UPDATED_DESCRIPTION);
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(savedActionPlan);

    // When
    ActionPlan actionPlanUpdate = new ActionPlan();
    actionPlanUpdate.setDescription(UPDATED_DESCRIPTION);
    ActionPlan updatedActionPlan =
        actionPlanServiceImpl.updateActionPlan(ACTION_PLAN_ID, actionPlanUpdate);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(any());
    assertEquals(updatedActionPlan.getDescription(), UPDATED_DESCRIPTION);
  }

  @Test
  public void testUpdateActionPlanRunTime() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlan savedActionPlan = mapperFacade.map(actionPlan, ActionPlan.class);
    savedActionPlan.setLastRunDateTime(UPDATED_TIMESTAMP);
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(savedActionPlan);

    // When
    ActionPlan actionPlanUpdate = new ActionPlan();
    actionPlanUpdate.setLastRunDateTime(UPDATED_TIMESTAMP);
    ActionPlan updatedActionPlan =
        actionPlanServiceImpl.updateActionPlan(ACTION_PLAN_ID, actionPlanUpdate);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(any());
    assertEquals(updatedActionPlan.getLastRunDateTime(), UPDATED_TIMESTAMP);
  }

  @Test
  public void testUpdateActionPlanSelectors() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlan savedActionPlan = mapperFacade.map(actionPlan, ActionPlan.class);
    savedActionPlan.setSelectors(selectors);
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(savedActionPlan);

    // When
    ActionPlan actionPlanUpdate = new ActionPlan();
    actionPlanUpdate.setSelectors(selectors);
    ActionPlan updatedActionPlan =
        actionPlanServiceImpl.updateActionPlan(ACTION_PLAN_ID, actionPlanUpdate);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(any());
    assertEquals(updatedActionPlan.getSelectors(), actionPlan.getSelectors());
  }
}
