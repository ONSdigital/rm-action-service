package uk.gov.ons.ctp.response.action.service;

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
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;

/** Tests for the ActionPlanJobServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanJobServiceTest {

  @InjectMocks private ActionPlanJobService actionPlanJobService;

  @Mock private ActionService actionService;
  @Mock private DistributedLockManager actionPlanExecutionLockManager;

  @Mock private ActionCaseRepository actionCaseRepo;
  @Mock private ActionPlanRepository actionPlanRepo;
  @Mock private ActionPlanJobRepository actionPlanJobRepo;

  private List<ActionPlan> actionPlans;
  private List<ActionPlanJob> actionPlanJobs;

  /** Initialises Mockito */
  @Before
  public void setUp() throws Exception {
    actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    actionPlanJobs = FixtureHelper.loadClassFixtures(ActionPlanJob[].class);

    MockitoAnnotations.initMocks(this);

    when(actionPlanRepo.findAll()).thenReturn(actionPlans);
    when(actionCaseRepo.countByActionPlanFK(any(Integer.class)))
      .thenReturn(Integer.toUnsignedLong(1));
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(true);
    when(actionPlanJobRepo.save(any(ActionPlanJob.class))).thenReturn(actionPlanJobs.get(0));
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobs() {

    // Given setUp()

    // When
    actionPlanJobService.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionPlanJobRepo, times(2)).save(any(ActionPlanJob.class));
    verify(actionService, times(2)).createScheduledActions(any(), any());
    verify(actionPlanExecutionLockManager, times(2)).unlock(actionPlans.get(0).getName());
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobsNoCases() {

    // Given
    when(actionCaseRepo.countByActionPlanFK(any(Integer.class)))
        .thenReturn(Integer.toUnsignedLong(0));

    // When
    actionPlanJobService.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionPlanJobRepo, never()).save(any(ActionPlanJob.class));
    verify(actionService, never()).createScheduledActions(any(), any());
    verify(actionPlanExecutionLockManager, never()).unlock(actionPlans.get(0).getName());
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobsFailToGetLock() {

    // Given
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(false);

    // When
    actionPlanJobService.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionPlanJobRepo, never()).save(any(ActionPlanJob.class));
    verify(actionService, never()).createScheduledActions(any(), any());
    verify(actionPlanExecutionLockManager, never()).unlock(actionPlans.get(0).getName());
  }
}
