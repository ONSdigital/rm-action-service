package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.PlanExecution;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;

/** Tests for the ActionPlanJobServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanJobServiceTest {

  @InjectMocks private ActionPlanJobService actionPlanJobService;

  @Spy private AppConfig appConfig = new AppConfig();
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

    final PlanExecution planExecution = new PlanExecution();
    planExecution.setDelayMilliSeconds(5000L);
    appConfig.setPlanExecution(planExecution);

    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test the service method called by the endpoint where exec is forced ie the service should
   * disregard last exec times
   */
  @Test
  public void testCreateAndExecuteActionPlanJob() {

    // Given
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(true);
    when(actionPlanJobRepo.save(any(ActionPlanJob.class))).thenReturn(actionPlanJobs.get(0));

    // When
    ActionPlanJob executedJob =
        actionPlanJobService.createAndExecuteActionPlanJob(actionPlans.get(0));

    // assert the right calls were made
    verify(actionPlanJobRepo, times(1)).save(any(ActionPlanJob.class));
    verify(actionService, times(1)).createScheduledActions(any(), any());
    verify(actionPlanExecutionLockManager, times(1)).unlock(actionPlans.get(0).getName());

    Assert.assertNotNull(executedJob);
  }

  /**
   * Test that the endpoint forced exec method gracefully handles the failure to lock an action plan
   */
  @Test
  public void testCreateAndExecuteActionPlanFailedLock() {

    // Given
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(false);

    // When
    ActionPlanJob executedJob =
        actionPlanJobService.createAndExecuteActionPlanJob(actionPlans.get(0));

    // Then
    assertNull(executedJob);
  }

  @Test
  public void testCreateAndExecuteAllActionPlanJobs() {

    // Given
    when(actionPlanRepo.findAll()).thenReturn(actionPlans);
    when(actionCaseRepo.countByActionPlanFK(any(Integer.class)))
        .thenReturn(Integer.toUnsignedLong(1));
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(true);
    when(actionPlanJobRepo.save(any(ActionPlanJob.class))).thenReturn(actionPlanJobs.get(0));

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
    when(actionPlanRepo.findAll()).thenReturn(actionPlans);
    when(actionCaseRepo.countByActionPlanFK(any(Integer.class)))
        .thenReturn(Integer.toUnsignedLong(0));

    // When
    actionPlanJobService.createAndExecuteAllActionPlanJobs();

    // Then
    verify(actionPlanJobRepo, never()).save(any(ActionPlanJob.class));
    verify(actionService, never()).createScheduledActions(any(), any());
    verify(actionPlanExecutionLockManager, never()).unlock(actionPlans.get(0).getName());
  }
}
