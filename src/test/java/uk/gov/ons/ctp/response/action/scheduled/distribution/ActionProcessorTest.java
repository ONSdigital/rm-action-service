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
public class ActionProcessorTest {

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

  @InjectMocks private ActionProcessor actionProcessor;

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
    ActionType actionType = actionTypes.get(2);
    when(actionTypeRepo.findByHandler(actionType.getHandler()))
        .thenReturn(Collections.singletonList(actionType));

    // When
    actionProcessor.processLetters();

    // Then
    verify(businessActionProcessingService, times(1))
        .processLetters(eq(actionType), any(List.class));
  }

  @Test
  public void testProcessActionRequestsThrowsCTPException() throws Exception {

    ActionType actionType = actionTypes.get(2);

    // Given
    when(actionTypeRepo.findByHandler(actionType.getHandler()))
        .thenReturn(Collections.singletonList(actionType));

    doThrow(CTPException.class)
        .when(businessActionProcessingService)
        .processLetters(actionType, actions);

    // When
    actionProcessor.processLetters();
  }

  @Test
  public void testNoCaseWithSampleUnitTypeB() throws Exception {

    ActionType actionType = actionTypes.get(2);

    // Given setUp
    when(actionTypeRepo.findByHandler(actionType.getHandler()))
        .thenReturn(Collections.singletonList(actionType));

    // When
    actionProcessor.processLetters();

    // Then
    verify(businessActionProcessingService, times(1))
        .processLetters(eq(actionType), any(List.class));
  }
}
