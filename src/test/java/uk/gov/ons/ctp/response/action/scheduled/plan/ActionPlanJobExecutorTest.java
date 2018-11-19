package uk.gov.ons.ctp.response.action.scheduled.plan;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.service.ActionService;

/** Tests for the ActionPlanJobServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanJobExecutorTest {

  @InjectMocks private ActionPlanJobExecutor actionPlanJobExecutor;

  @Mock private ActionService actionService;
  @Mock private DistributedLockManager actionPlanExecutionLockManager;

  @Mock private ActionCaseRepository actionCaseRepo;
  @Mock private ActionPlanRepository actionPlanRepo;

  private List<ActionPlan> actionPlans;

  /** Initialises Mockito */
  @Before
  public void setUp() throws Exception {
    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);

    MockitoAnnotations.initMocks(this);

    when(actionPlanRepo.findAll()).thenReturn(actionPlans);
    when(actionCaseRepo.existsByActionPlanFK(any(Integer.class))).thenReturn(true);
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(true);
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobs() {

    // Given setUp()

    // When
    actionPlanJobExecutor.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionService, times(2)).createScheduledActions(any());
    verify(actionPlanExecutionLockManager, times(2)).unlock(actionPlans.get(0).getName());
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobsNoCases() {

    // Given
    when(actionCaseRepo.existsByActionPlanFK(any(Integer.class))).thenReturn(false);

    // When
    actionPlanJobExecutor.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionService, never()).createScheduledActions(any());
    verify(actionPlanExecutionLockManager, never()).unlock(actionPlans.get(0).getName());
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobsFailToGetLock() {

    // Given
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(false);

    // When
    actionPlanJobExecutor.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionService, never()).createScheduledActions(any());
    verify(actionPlanExecutionLockManager, never()).unlock(actionPlans.get(0).getName());
  }
}
