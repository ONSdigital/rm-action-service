package uk.gov.ons.ctp.response.action.scheduled.distribution;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.config.ActionDistribution;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;

/**
 * Test the ActionDistributor
 *
 * <p>Important reminder on the standing data held in json files: - case
 * 3382981d-3df0-464e-9c95-aea7aee80c81 is linked with a SUBMITTED action so expect 1 ActionRequest
 * - case 3382981d-3df0-464e-9c95-aea7aee80c82 is linked with a CANCEL_SUBMITTED action so expect 1
 * ActionCancel - case 3382981d-3df0-464e-9c95-aea7aee80c83 is linked with a SUBMITTED action so
 * expect 1 ActionRequest - case 3382981d-3df0-464e-9c95-aea7aee80c84 is linked with a
 * CANCEL_SUBMITTED action so expect 1 ActionCancel - all actions have responseRequired = true
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ActionDistributorTest {

  private static final int TEN = 10;

  private static final String HOUSEHOLD_INITIAL_CONTACT = "HouseholdInitialContact";
  private static final String HOUSEHOLD_UPLOAD_IAC = "HouseholdUploadIAC";

  private List<ActionType> actionTypes;
  private List<Action> householdInitialContactActions;
  private List<Action> householdUploadIACActions;

  @Spy private AppConfig appConfig = new AppConfig();

  @Mock private ActionRepository actionRepo;

  @Mock private ActionTypeRepository actionTypeRepo;

  @Mock private ActionProcessingService actionProcessingService;

  @Mock private ActionCaseRepository actionCaseRepo;

  @InjectMocks private ActionDistributor actionDistributor;

  /** Initialises Mockito and loads Class Fixtures */
  @Before
  public void setUp() throws Exception {
    final ActionDistribution actionDistributionConfig = new ActionDistribution();
    actionDistributionConfig.setDelayMilliSeconds(TEN);
    actionDistributionConfig.setRetrievalMax(TEN);
    actionDistributionConfig.setRetrySleepSeconds(TEN);
    appConfig.setActionDistribution(actionDistributionConfig);

    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
    householdInitialContactActions =
        FixtureHelper.loadClassFixtures(Action[].class, HOUSEHOLD_INITIAL_CONTACT);
    householdUploadIACActions =
        FixtureHelper.loadClassFixtures(Action[].class, HOUSEHOLD_UPLOAD_IAC);

    MockitoAnnotations.initMocks(this);
  }

  /**
   * We retrieve actionTypes but then exception thrown when retrieving actions.
   *
   * @throws Exception oops
   */
  @Test
  public void testFailToGetAnyAction() throws Exception {
    when(actionTypeRepo.findAll()).thenReturn(actionTypes);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(any(String.class), anyInt()))
        .thenThrow(new RuntimeException("Database access failed"));

    final DistributionInfo info = actionDistributor.distribute();
    final List<InstructionCount> countList = info.getInstructionCounts();
    assertEquals(4, countList.size());
    final List<InstructionCount> expectedCountList = new ArrayList<>();
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(
            HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.CANCEL_REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.CANCEL_REQUEST, 0));
    assertTrue(countList.equals(expectedCountList));

    // Assertions for calls in method retrieveActions
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_INITIAL_CONTACT), anyInt());
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt());

    // Assertions for calls in actionProcessingService
    verify(actionProcessingService, times(0)).processActionRequest(any(Action.class));
    verify(actionProcessingService, times(0)).processActionCancel(any(Action.class));
  }

  /** We retrieve no actionTypes so no exception should be thrown. */
  @Test
  public void testExceptionNotThrowWhenNoActionTypes() {
    when(actionTypeRepo.findAll()).thenReturn(new ArrayList<>());
    final DistributionInfo info = actionDistributor.distribute();
    assertEquals(new DistributionInfo(), info);
  }

  /**
   * Happy Path with 2 ActionRequests and 2 ActionCancels for a H case (ie parent case)
   *
   * @throws Exception oops
   */
  @Test
  public void testHappyPathParentCase() throws Exception {
    ActionCase acase = new ActionCase();
    acase.setSampleUnitType("B");
    when(actionCaseRepo.findById(any())).thenReturn(acase);

    when(actionTypeRepo.findAll()).thenReturn(actionTypes);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(
            eq(HOUSEHOLD_INITIAL_CONTACT), anyInt()))
        .thenReturn(householdInitialContactActions);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt()))
        .thenReturn(householdUploadIACActions);

    final DistributionInfo info = actionDistributor.distribute();
    final List<InstructionCount> countList = info.getInstructionCounts();
    assertEquals(4, countList.size());
    final List<InstructionCount> expectedCountList = new ArrayList<>();
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.REQUEST, 1));
    expectedCountList.add(
        new InstructionCount(
            HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.CANCEL_REQUEST, 1));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.REQUEST, 1));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.CANCEL_REQUEST, 1));
    assertTrue(countList.equals(expectedCountList));

    verify(actionTypeRepo).findAll();

    // Assertions for calls in method retrieveActions
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_INITIAL_CONTACT), anyInt());
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt());

    // Assertions for calls to actionProcessingService & processActionRequest
    final ArgumentCaptor<Action> actionCaptorForActionRequest =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionRequest(actionCaptorForActionRequest.capture());
    List<Action> actionsList = actionCaptorForActionRequest.getAllValues();
    assertEquals(2, actionsList.size());
    List<Action> expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(0));
    expectedActionsList.add(householdUploadIACActions.get(0));
    assertTrue(expectedActionsList.equals(actionsList));

    // Assertions for calls to actionProcessingService & processActionCancel
    final ArgumentCaptor<Action> actionCaptorForActionCancel =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionCancel(actionCaptorForActionCancel.capture());
    actionsList = actionCaptorForActionCancel.getAllValues();
    assertEquals(2, actionsList.size());
    expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(1));
    expectedActionsList.add(householdUploadIACActions.get(1));
    assertTrue(expectedActionsList.equals(actionsList));
  }

  /**
   * Test with 2 ActionRequests and 2 ActionCancels for a H case (ie parent case) where
   * ActionProcessingService throws an Exception when processActionRequest and when
   * processActionCancel
   *
   * @throws Exception oops
   */
  @Test
  public void testActionProcessingServiceThrowsException() throws Exception {

    when(actionTypeRepo.findAll()).thenReturn(actionTypes);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(
            eq(HOUSEHOLD_INITIAL_CONTACT), anyInt()))
        .thenReturn(householdInitialContactActions);
    ActionCase acase = new ActionCase();
    acase.setSampleUnitType("B");
    when(actionCaseRepo.findById(any())).thenReturn(acase);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt()))
        .thenReturn(householdUploadIACActions);
    doThrow(new RuntimeException("Database access failed"))
        .when(actionProcessingService)
        .processActionRequest(any(Action.class));
    doThrow(new RuntimeException("Database access failed"))
        .when(actionProcessingService)
        .processActionCancel(any(Action.class));

    final DistributionInfo info = actionDistributor.distribute();
    final List<InstructionCount> countList = info.getInstructionCounts();
    assertEquals(4, countList.size());
    final List<InstructionCount> expectedCountList = new ArrayList<>();
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(
            HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.CANCEL_REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.CANCEL_REQUEST, 0));
    assertTrue(countList.equals(expectedCountList));

    verify(actionTypeRepo).findAll();

    // Assertions for calls in method retrieveActions
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_INITIAL_CONTACT), anyInt());
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt());

    // Assertions for calls to actionProcessingService & processActionRequest
    final ArgumentCaptor<Action> actionCaptorForActionRequest =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionRequest(actionCaptorForActionRequest.capture());
    List<Action> actionsList = actionCaptorForActionRequest.getAllValues();
    assertEquals(2, actionsList.size());
    List<Action> expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(0));
    expectedActionsList.add(householdUploadIACActions.get(0));
    assertTrue(expectedActionsList.equals(actionsList));

    // Assertions for calls to actionProcessingService & processActionCancel
    final ArgumentCaptor<Action> actionCaptorForActionCancel =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionCancel(actionCaptorForActionCancel.capture());
    actionsList = actionCaptorForActionCancel.getAllValues();
    assertEquals(2, actionsList.size());
    expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(1));
    expectedActionsList.add(householdUploadIACActions.get(1));
    assertTrue(expectedActionsList.equals(actionsList));
  }

  /**
   * Test with 2 ActionRequests and 2 ActionCancels for a H case (ie parent case) where
   * ActionProcessingService throws an Exception intermittently when processActionRequest and when
   * processActionCancel - processActionRequest KO for actionPK = 1 (HOUSEHOLD_INITIAL_CONTACT) -
   * processActionRequest OK for actionPK = 3 (HOUSEHOLD_UPLOAD_IAC) - processActionCancel OK for
   * actionPK = 2 (HOUSEHOLD_INITIAL_CONTACT) - processActionCancel KO for actionPK = 4
   * (HOUSEHOLD_UPLOAD_IAC)
   *
   * @throws Exception oops
   */
  @Test
  public void testActionProcessingServiceThrowsExceptionIntermittently() throws Exception {
    ActionCase acase = new ActionCase();
    acase.setSampleUnitType("B");
    when(actionCaseRepo.findById(any())).thenReturn(acase);

    when(actionTypeRepo.findAll()).thenReturn(actionTypes);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(
            eq(HOUSEHOLD_INITIAL_CONTACT), anyInt()))
        .thenReturn(householdInitialContactActions);
    when(actionRepo.findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt()))
        .thenReturn(householdUploadIACActions);
    doThrow(new RuntimeException("Database access failed"))
        .when(actionProcessingService)
        .processActionRequest(eq(householdInitialContactActions.get(0)));
    doThrow(new RuntimeException("Database access failed"))
        .when(actionProcessingService)
        .processActionCancel(eq(householdUploadIACActions.get(1)));

    final DistributionInfo info = actionDistributor.distribute();
    final List<InstructionCount> countList = info.getInstructionCounts();
    assertEquals(4, countList.size());
    final List<InstructionCount> expectedCountList = new ArrayList<>();
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.REQUEST, 0));
    expectedCountList.add(
        new InstructionCount(
            HOUSEHOLD_INITIAL_CONTACT, DistributionInfo.Instruction.CANCEL_REQUEST, 1));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.REQUEST, 1));
    expectedCountList.add(
        new InstructionCount(HOUSEHOLD_UPLOAD_IAC, DistributionInfo.Instruction.CANCEL_REQUEST, 0));
    assertTrue(countList.equals(expectedCountList));

    verify(actionTypeRepo).findAll();

    // Assertions for calls in method retrieveActions
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_INITIAL_CONTACT), anyInt());
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt());

    // Assertions for calls to actionProcessingService & processActionRequest
    final ArgumentCaptor<Action> actionCaptorForActionRequest =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionRequest(actionCaptorForActionRequest.capture());
    List<Action> actionsList = actionCaptorForActionRequest.getAllValues();
    assertEquals(2, actionsList.size());
    List<Action> expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(0));
    expectedActionsList.add(householdUploadIACActions.get(0));
    assertTrue(expectedActionsList.equals(actionsList));

    // Assertions for calls to actionProcessingService & processActionCancel
    final ArgumentCaptor<Action> actionCaptorForActionCancel =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionCancel(actionCaptorForActionCancel.capture());
    actionsList = actionCaptorForActionCancel.getAllValues();
    assertEquals(2, actionsList.size());
    expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(1));
    expectedActionsList.add(householdUploadIACActions.get(1));
    assertTrue(expectedActionsList.equals(actionsList));
  }
}
