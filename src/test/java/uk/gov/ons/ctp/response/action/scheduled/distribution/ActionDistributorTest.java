package uk.gov.ons.ctp.response.action.scheduled.distribution;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
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

@RunWith(MockitoJUnitRunner.class)
public class ActionDistributorTest {

  private static final String SOCIALNOT = "SOCIALNOT";
  private static final String SOCIALSNE = "SOCIALSNE";
  private static final String BSNOT = "BSNOT";

  private List<ActionType> actionTypes;
  private List<Action> actions;
  private Stream<Action> socialNotificationActions;
  private Stream<Action> socialRemindersActions;
  private Stream<Action> businessEnrolmentActions;
  private ActionCase bActionCase;
  private ActionCase hActionCase;
  private ActionCase fActionCase;
  private RLock lock;

  @Mock private AppConfig appConfig;

  @Mock private RedissonClient redissonClient;

  @Mock private ActionRepository actionRepo;

  @Mock private ActionTypeRepository actionTypeRepo;

  @Mock(name = "businessActionProcessingService")
  private ActionProcessingService businessActionProcessingService;

  @Mock(name = "socialActionProcessingService")
  private ActionProcessingService socialActionProcessingService;

  @Mock private ActionCaseRepository actionCaseRepo;

  @InjectMocks private ActionDistributor actionDistributor;

  /** Initialises Mockito and loads Class Fixtures */
  @Before
  public void setUp() throws Exception {
    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
    actions = FixtureHelper.loadClassFixtures(Action[].class);
    socialNotificationActions = actions.subList(0, 2).stream();
    socialRemindersActions = actions.subList(2, 4).stream();
    businessEnrolmentActions = actions.subList(4, 6).stream();
    bActionCase = new ActionCase();
    bActionCase.setSampleUnitType("B");
    hActionCase = new ActionCase();
    hActionCase.setSampleUnitType("H");
    fActionCase = new ActionCase();
    fActionCase.setSampleUnitType("F");

    MockitoAnnotations.initMocks(this);
    DataGrid dataGrid = new DataGrid();
    dataGrid.setLockTimeToLiveSeconds(30);
    dataGrid.setLockTimeToWaitSeconds(600);
    when(appConfig.getDataGrid()).thenReturn(dataGrid);
    ActionDistribution actionDistribution = new ActionDistribution();
    when(appConfig.getActionDistribution()).thenReturn(actionDistribution);

    lock = mock(RLock.class);
    when(redissonClient.getFairLock(any())).thenReturn(lock);
    when(lock.tryLock(anyInt(), eq(TimeUnit.SECONDS))).thenReturn(true);
    when(actionCaseRepo.findById(any())).thenReturn(bActionCase);

    // 3 action types (SOCIALNOT, SOCIALSNE, BSNOT)
    when(actionTypeRepo.findAll()).thenReturn(actionTypes);

    for (ActionType actionType : actionTypes) {
      if (actionType.getName().equals(SOCIALNOT)) {
        when(actionRepo.findByActionTypeAndStateIn(eq(actionType), any()))
            .thenReturn(socialNotificationActions);
      } else if (actionType.getName().equals(SOCIALSNE)) {
        when(actionRepo.findByActionTypeAndStateIn(eq(actionType), any()))
            .thenReturn(socialRemindersActions);
      } else if (actionType.getName().equals(BSNOT)) {
        when(actionRepo.findByActionTypeAndStateIn(eq(actionType), any()))
            .thenReturn(businessEnrolmentActions);
      }
    }
  }

  /** Happy Path with 1 ActionRequest and 1 ActionCancel for a B case */
  @Test
  public void testHappyPathBCase() throws Exception {
    // Given setUp
    when(actionTypeRepo.findAll()).thenReturn(Collections.singletonList(actionTypes.get(2)));

    // When
    actionDistributor.distribute();

    // Then
    verify(businessActionProcessingService, times(1)).processActionRequests(any());
    verify(businessActionProcessingService, times(1)).processActionCancel(any());

    verify(lock, times(1)).unlock();
  }

  /** Happy Path with 2 ActionRequests and 2 ActionCancels for a H case */
  @Test
  public void testHappyPathHCase() throws Exception {
    // Given
    when(actionTypeRepo.findAll()).thenReturn(actionTypes.subList(0, 2));
    when(actionCaseRepo.findById(any())).thenReturn(hActionCase);

    // When
    actionDistributor.distribute();

    // Then
    verify(socialActionProcessingService, times(2)).processActionRequests(any());
    verify(socialActionProcessingService, times(2)).processActionCancel(any());

    verify(lock, times(2)).unlock();
  }

  @Test
  public void testProcessActionRequestsThrowsCTPException() throws Exception {
    // Given
    when(actionTypeRepo.findAll()).thenReturn(Collections.singletonList(actionTypes.get(2)));
    doThrow(CTPException.class).when(businessActionProcessingService).processActionRequests(any());

    // When
    actionDistributor.distribute();

    // Then
    verify(lock, times(1)).unlock();
  }
}
