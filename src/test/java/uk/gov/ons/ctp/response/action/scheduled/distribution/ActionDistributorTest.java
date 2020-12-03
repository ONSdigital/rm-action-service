package uk.gov.ons.ctp.response.action.scheduled.distribution;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.redisson.api.RedissonClient;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;

@RunWith(MockitoJUnitRunner.class)
public class ActionDistributorTest {

  private static final String BSNOT = "BSNOT";

  private List<ActionType> actionTypes;
  private List<Action> actions;
  private Stream<Action> businessEnrolmentActions;

  @Mock private AppConfig appConfig;

  @Mock private RedissonClient redissonClient;

  @Mock private ActionRepository actionRepo;

  @Mock private ActionTypeRepository actionTypeRepo;

  @Mock private StateTransitionManager<ActionState, ActionEvent> actionSvcStateTransitionManager;

  @Mock private ActionProcessingService businessActionProcessingService;

  @Mock private ActionCaseRepository actionCaseRepo;

  @InjectMocks private ActionDistributor actionDistributor;

  /** Initialises Mockito and loads Class Fixtures */
  @Before
  public void setUp() throws Exception {
    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
    actions = FixtureHelper.loadClassFixtures(Action[].class);
    businessEnrolmentActions = actions.subList(4, 6).stream();
    MockitoAnnotations.initMocks(this);
    when(actionTypeRepo.findAll()).thenReturn(actionTypes);

    for (ActionType actionType : actionTypes) {
      when(actionRepo.findByActionTypeAndStateIn(eq(actionType), any()))
          .thenReturn(businessEnrolmentActions);
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
    verify(businessActionProcessingService, times(1))
        .processActions(eq(actionTypes.get(2)), any(List.class));
  }

  @Test
  public void testProcessActionRequestsThrowsCTPException() throws Exception {
    // Given
    when(actionTypeRepo.findAll()).thenReturn(Collections.singletonList(actionTypes.get(2)));
    doThrow(CTPException.class)
        .when(businessActionProcessingService)
        .processActions(actionTypes.get(2), actions);

    // When
    actionDistributor.distribute();
  }

  @Test
  public void testNoCaseWithSampleUnitTypeB() throws Exception {
    // Given setUp
    when(actionTypeRepo.findAll()).thenReturn(Collections.singletonList(actionTypes.get(2)));

    // When
    actionDistributor.distribute();

    // Then
    verify(businessActionProcessingService, times(1))
        .processActions(eq(actionTypes.get(2)), any(List.class));
  }
}
