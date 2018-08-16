package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.PlanExecution;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;

/** Tests for the ActionPlanJobServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionPlanJobServiceTest {

  @Mock private DistributedLockManager actionPlanExecutionLockManager;

  @Spy private AppConfig appConfig = new AppConfig();

  @Mock private ActionPlanRepository actionPlanRepo;

  @Mock private ActionCaseRepository actionCaseRepo;

  @Mock private ActionPlanJobRepository actionPlanJobRepo;

  @Mock private ActionService actionService;

  @InjectMocks private ActionPlanJobService actionPlanJobService;

  /** Initialises Mockito */
  @Before
  public void setUp() throws Exception {
    final PlanExecution planExecution = new PlanExecution();
    planExecution.setDelayMilliSeconds(5000L);
    appConfig.setPlanExecution(planExecution);
    MockitoAnnotations.initMocks(this);

    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(true);
  }

  /**
   * Test the service method called by the endpoint where exec is forced ie the service should
   * disregard last exec times
   */
  @Test
  public void testCreateAndExecuteActionPlanJobForcedExecutionBlueSky() throws Exception {
    // load fixtures
    final List<ActionPlan> actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    final List<ActionPlanJob> actionPlanJobs =
        FixtureHelper.loadClassFixtures(ActionPlanJob[].class);
    final List<ActionCase> actionCases = FixtureHelper.loadClassFixtures(ActionCase[].class);

    // wire up mock responses
    when(actionPlanRepo.findOne(1)).thenReturn(actionPlans.get(0));
    when(actionCaseRepo.countByActionPlanFK(1)).thenReturn(new Long(actionCases.size()));
    when(actionPlanJobRepo.save(actionPlanJobs.get(0))).thenReturn(actionPlanJobs.get(0));

    // let it roll
    final ActionPlanJob executedJob =
        actionPlanJobService.createAndExecuteActionPlanJob(actionPlanJobs.get(0));

    // assert the right calls were made
    verify(actionPlanRepo).findOne(1);
    verify(actionCaseRepo).countByActionPlanFK(1);

    final ArgumentCaptor<ActionPlanJob> actionPlanJob =
        ArgumentCaptor.forClass(ActionPlanJob.class);
    verify(actionPlanJobRepo).save(actionPlanJob.capture());
    final ActionPlanJob savedJob = actionPlanJob.getValue();
    assertEquals(actionPlanJobs.get(0), savedJob);

    verify(actionService).createScheduledActions(1);

    Assert.assertNotNull(executedJob);
  }

  /**
   * Test that the endpoint forced exec method gracefully handles the failure to lock an action plan
   */
  @Test
  public void testCreateAndExecuteActionPlanJobForcedExecutionFailedLock() throws Exception {

    // set up mock hazelcast with a lock that will fail
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(false);

    // load fixtures
    final List<ActionPlan> actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    final List<ActionPlanJob> actionPlanJobs =
        FixtureHelper.loadClassFixtures(ActionPlanJob[].class);

    // wire up mock responses
    when(actionPlanRepo.findOne(1)).thenReturn(actionPlans.get(0));
    when(actionCaseRepo.countByActionPlanFK(1)).thenReturn(1L);

    // let it roll
    final ActionPlanJob executedJob =
        actionPlanJobService.createAndExecuteActionPlanJob(actionPlanJobs.get(0));

    // assert the right calls were made
    verify(actionPlanJobRepo, times(0)).save(actionPlanJobs.get(0));
    verify(actionService, times(0)).createScheduledActions(1);
    Assert.assertNull(executedJob);
  }

  /**
   * Test that the endpoint forced exec method gracefully handles the failure to lock an action plan
   */
  @Test
  public void testCreateAndExecuteActionPlanJobActionNotFound() throws Exception {

    // set up mock hazelcast with a lock that will fail
    when(actionPlanExecutionLockManager.lock(any(String.class))).thenReturn(false);

    // load fixtures
    final List<ActionPlanJob> actionPlanJobs =
        FixtureHelper.loadClassFixtures(ActionPlanJob[].class);

    // wire up mock responses
    when(actionPlanRepo.findOne(1)).thenReturn(null);

    // let it roll
    final ActionPlanJob executedJob =
        actionPlanJobService.createAndExecuteActionPlanJob(actionPlanJobs.get(0));

    Assert.assertNull(executedJob);
  }

  /** Test the endpoint forced exec method handles no open cases for an action plan gracefully */
  @Test
  public void testCreateAndExecuteActionPlanJobForcedExecutionNoCases() throws Exception {

    // load fixtures
    final List<ActionPlan> actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);
    final List<ActionPlanJob> actionPlanJobs =
        FixtureHelper.loadClassFixtures(ActionPlanJob[].class);
    final List<ActionCase> actionCases = new ArrayList<>();

    // wire up mock responses
    when(actionPlanRepo.findOne(1)).thenReturn(actionPlans.get(0));
    when(actionCaseRepo.countByActionPlanFK(1)).thenReturn(new Long(actionCases.size()));

    // let it roll
    final ActionPlanJob executedJob =
        actionPlanJobService.createAndExecuteActionPlanJob(actionPlanJobs.get(0));

    // assert the right calls were made
    verify(actionPlanRepo).findOne(1);
    verify(actionCaseRepo).countByActionPlanFK(1);
    verify(actionPlanJobRepo, times(0)).save(actionPlanJobs.get(0));
    verify(actionService, times(0)).createScheduledActions(1);

    Assert.assertNull(executedJob);
  }

  /**
   * Test that the service method that execs ALL plans works when all plans require running due to
   * expired last run times
   */
  @Test
  public void testCreateAndExecuteActionPlanJobUnForcedExecutionPlanDoesRun() throws Exception {

    // load fixtures
    final List<ActionPlan> actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);

    // set fixture actionplans to have run 10s ago
    final Timestamp now = DateTimeUtil.nowUTC();
    final Timestamp lastExecutionTime = new Timestamp(now.getTime() - 10000);
    actionPlans.forEach(actionPlan -> actionPlan.setLastRunDateTime(lastExecutionTime));

    final List<ActionPlanJob> actionPlanJobs =
        FixtureHelper.loadClassFixtures(ActionPlanJob[].class);

    // wire up mock responses
    when(actionPlanRepo.findAll()).thenReturn(actionPlans);
    when(actionPlanRepo.findOne(1)).thenReturn(actionPlans.get(0));
    when(actionPlanRepo.findOne(2)).thenReturn(actionPlans.get(1));
    when(actionCaseRepo.countByActionPlanFK(1)).thenReturn(1L);
    when(actionCaseRepo.countByActionPlanFK(2)).thenReturn(1L);
    when(actionPlanJobRepo.save(any(ActionPlanJob.class))).thenReturn(actionPlanJobs.get(0));

    // let it roll
    final List<ActionPlanJob> executedJobs =
        actionPlanJobService.createAndExecuteAllActionPlanJobs();

    // assert the right calls were made
    verify(actionPlanRepo, times(1)).findAll();
    verify(actionPlanRepo, times(1)).findOne(1);
    verify(actionPlanRepo, times(1)).findOne(2);
    verify(actionCaseRepo, times(1)).countByActionPlanFK(1);
    verify(actionCaseRepo, times(1)).countByActionPlanFK(2);
    verify(actionPlanJobRepo, times(2)).save(any(ActionPlanJob.class));
    verify(actionService, times(2)).createScheduledActions(any(Integer.class));

    Assert.assertTrue(executedJobs.size() > 0);
  }

  @Test
  public void testCreateAndExecuteActionPlanJobPlanDoesNotRun() throws Exception {

    // load fixtures
    final List<ActionPlan> actionPlans = FixtureHelper.loadClassFixtures(ActionPlan[].class);

    // set fixture actionplans to have run 1s ago
    final Timestamp now = DateTimeUtil.nowUTC();
    final Timestamp lastExecutionTime = new Timestamp(now.getTime() - 1000);
    actionPlans.forEach(actionPlan -> actionPlan.setLastRunDateTime(lastExecutionTime));

    // wire up mock responses
    when(actionPlanRepo.findAll()).thenReturn(actionPlans);
    when(actionPlanRepo.findOne(1)).thenReturn(actionPlans.get(0));
    when(actionPlanRepo.findOne(2)).thenReturn(actionPlans.get(1));

    // let it roll
    final List<ActionPlanJob> executedJobs =
        actionPlanJobService.createAndExecuteAllActionPlanJobs();

    // assert the right calls were made
    verify(actionPlanJobRepo, times(0)).save(any(ActionPlanJob.class));
    verify(actionService, times(0)).createScheduledActions(any(Integer.class));

    Assert.assertFalse(executedJobs.size() > 0);
  }
}
