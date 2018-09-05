package uk.gov.ons.ctp.response.action.scheduled.distribution;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.config.ActionDistribution;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.DataGrid;
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
public class ActionDistributorTest {

  private static final String HOUSEHOLD_INITIAL_CONTACT = "HouseholdInitialContact";
  private static final String HOUSEHOLD_UPLOAD_IAC = "HouseholdUploadIAC";

  private List<ActionType> actionTypes;
  private List<Action> householdInitialContactActions;
  private List<Action> householdUploadIACActions;
  private ActionCase actionCase;
  private RLock lock;

  @Mock private AppConfig appConfig;

  @Mock private RedissonClient redissonClient;

  @Mock private ActionRepository actionRepo;

  @Mock private ActionTypeRepository actionTypeRepo;

  @Mock private ActionProcessingService actionProcessingService;

  @Mock private ActionCaseRepository actionCaseRepo;

  @InjectMocks private ActionDistributor actionDistributor;

  /** Initialises Mockito and loads Class Fixtures */
  @Before
  public void setUp() throws Exception {
    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
    householdInitialContactActions =
        FixtureHelper.loadClassFixtures(Action[].class, HOUSEHOLD_INITIAL_CONTACT);
    householdUploadIACActions =
        FixtureHelper.loadClassFixtures(Action[].class, HOUSEHOLD_UPLOAD_IAC);
    actionCase = new ActionCase();
    actionCase.setSampleUnitType("H");

    MockitoAnnotations.initMocks(this);
    DataGrid dataGrid = new DataGrid();
    dataGrid.setLockTimeToLiveSeconds(30);
    dataGrid.setLockTimeToWaitSeconds(600);
    when(appConfig.getDataGrid()).thenReturn(dataGrid);
    ActionDistribution actionDistribution = new ActionDistribution();
    actionDistribution.setRetrievalMax(1000);
    when(appConfig.getActionDistribution()).thenReturn(actionDistribution);

    lock = mock(RLock.class);
    when(redissonClient.getFairLock(any())).thenReturn(lock);
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
  }

  /** We retrieve no actionTypes so no exception should be thrown. */
  @Test
  public void testExceptionNotThrowWhenNoActionTypes() {
    when(actionTypeRepo.findAll()).thenReturn(new ArrayList<>());
    actionDistributor.distribute();
  }

  /**
   * Happy Path with 2 ActionRequests and 2 ActionCancels for a H case
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

    actionDistributor.distribute();

    verify(actionTypeRepo).findAll();
    // Assertions for calls in method retrieveActions
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_INITIAL_CONTACT), anyInt());
    verify(actionRepo, times(1))
        .findSubmittedOrCancelledByActionTypeName(eq(HOUSEHOLD_UPLOAD_IAC), anyInt());
    verify(lock, times(2)).unlock();

    // Assertions for calls to actionProcessingService & processActionRequests
    final ArgumentCaptor<Action> actionCaptorForActionRequest =
        ArgumentCaptor.forClass(Action.class);
    verify(actionProcessingService, times(2))
        .processActionRequests(actionCaptorForActionRequest.capture());
    List<Action> actionsList = actionCaptorForActionRequest.getAllValues();
    assertEquals(2, actionsList.size());
    List<Action> expectedActionsList = new ArrayList<>();
    expectedActionsList.add(householdInitialContactActions.get(0));
    expectedActionsList.add(householdUploadIACActions.get(0));
    assertEquals(expectedActionsList, actionsList);

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
    assertEquals(expectedActionsList, actionsList);
  }
}
