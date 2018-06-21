package uk.gov.ons.ctp.response.action.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanSelector;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanSelectorRepository;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the ActionPlanJobServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanServiceImplTest {

  @InjectMocks
  private ActionPlanServiceImpl actionPlanServiceImpl;

  @Mock
  private ActionPlanRepository actionPlanRepo;

  @Mock
  private ActionPlanSelectorRepository actionPlanSelectorRepo;

  @Spy
  private MapperFacade mapperFacade = new ActionBeanMapper();

  private List<ActionPlan> actionPlans;

  private static final UUID ACTION_PLAN_ID = UUID.fromString("e71002ac-3575-47eb-b87f-cd9db92bf9a7");
  private static final String UPDATED_DESCRIPTION = "New description";
  private static final Date UPDATED_DATE = new Date();
  private static final Timestamp UPDATED_TIMESTAMP = new Timestamp(UPDATED_DATE.getTime());

  /**
   * Before the test
   */
  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
  }

  @Test
  public void testCreateActionPlanNoSelectors() {
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(actionPlan);

    ActionPlanDTO actionPlanDTO = actionPlanServiceImpl.createActionPlan(actionPlan, new ActionPlanSelector());

    verify(actionPlanRepo, times(1)).saveAndFlush(actionPlan);
    verify(actionPlanSelectorRepo, times(0)).saveAndFlush(any());
    assertEquals(actionPlanDTO.getName(), actionPlans.get(0).getName());
  }

  @Test
  public void testCreateActionPlanWithSelectors() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.saveAndFlush(any())).thenReturn(actionPlan);

    ActionPlanSelector actionPlanSelector = new ActionPlanSelector();
    HashMap<String, String> selectors = new HashMap<>();
    selectors.put("testKey", "testValue");
    actionPlanSelector.setSelectors(selectors);
    when(actionPlanSelectorRepo.saveAndFlush(any())).thenReturn(actionPlanSelector);

    // When
    ActionPlanDTO actionPlanDTO = actionPlanServiceImpl.createActionPlan(actionPlan, actionPlanSelector);

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(actionPlan);
    verify(actionPlanSelectorRepo, times(1)).saveAndFlush(actionPlanSelector);
    assertEquals(actionPlanDTO.getName(), actionPlans.get(0).getName());
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
    ActionPlan updatedActionPlan = actionPlanServiceImpl.updateActionPlan(
            ACTION_PLAN_ID, actionPlanUpdate, new ActionPlanSelector());

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(any());
    verify(actionPlanSelectorRepo, times(0)).saveAndFlush(any());
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
    ActionPlan updatedActionPlan = actionPlanServiceImpl.updateActionPlan(
            ACTION_PLAN_ID, actionPlanUpdate, new ActionPlanSelector());

    // Then
    verify(actionPlanRepo, times(1)).saveAndFlush(any());
    verify(actionPlanSelectorRepo, times(0)).saveAndFlush(any());
    assertEquals(updatedActionPlan.getLastRunDateTime(), UPDATED_TIMESTAMP);
  }

  @Test
  public void testUpdateActionPlanSelectorsExistingSelector() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlanSelector actionPlanSelector = new ActionPlanSelector();
    when(actionPlanSelectorRepo.findFirstByActionPlanFk(actionPlan.getActionPlanPK())).thenReturn(actionPlanSelector);

    ActionPlanSelector savedActionPlanSelector = mapperFacade.map(actionPlanSelector, ActionPlanSelector.class);
    HashMap<String, String> selectors = new HashMap<>();
    selectors.put("testKey", "testValue");
    savedActionPlanSelector.setSelectors(selectors);
    when(actionPlanSelectorRepo.saveAndFlush(any())).thenReturn(savedActionPlanSelector);

    // When
    ActionPlanSelector actionPlanSelectorUpdate = new ActionPlanSelector();
    actionPlanSelectorUpdate.setSelectors(selectors);
    ActionPlan updatedActionPlan = actionPlanServiceImpl.updateActionPlan(
            ACTION_PLAN_ID, new ActionPlan(), actionPlanSelectorUpdate);

    // Then
    verify(actionPlanRepo, times(0)).saveAndFlush(any());
    verify(actionPlanSelectorRepo, times(1)).saveAndFlush(any());
    assertEquals(updatedActionPlan.getName(), actionPlan.getName());
  }

  @Test
  public void testUpdateActionPlanSelectorsNoExistingSelector() {
    // Given
    ActionPlan actionPlan = actionPlans.get(0);
    when(actionPlanRepo.findById(ACTION_PLAN_ID)).thenReturn(actionPlan);

    ActionPlanSelector actionPlanSelector = new ActionPlanSelector();
    when(actionPlanSelectorRepo.findFirstByActionPlanFk(actionPlan.getActionPlanPK())).thenReturn(actionPlanSelector);
    when(actionPlanSelectorRepo.saveAndFlush(any())).thenReturn(null);

    // When
    ActionPlanSelector actionPlanSelectorUpdate = new ActionPlanSelector();
    HashMap<String, String> selectors = new HashMap<>();
    selectors.put("testKey", "testValue");
    actionPlanSelectorUpdate.setSelectors(selectors);
    ActionPlan updatedActionPlan = actionPlanServiceImpl.updateActionPlan(
            ACTION_PLAN_ID, new ActionPlan(), actionPlanSelectorUpdate);

    // Then
    verify(actionPlanRepo, times(0)).saveAndFlush(any());
    verify(actionPlanSelectorRepo, times(1)).saveAndFlush(any());
    assertEquals(updatedActionPlan.getName(), actionPlan.getName());
  }

}
