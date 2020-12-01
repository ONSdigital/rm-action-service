package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.CaseSvc;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO.SampleUnitType;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

/** Tests for the ActionProcessingServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionProcessingServiceTest {

  private static final Integer ACTION_PLAN_FK = 1;

  private static final String PRINTER = "Printer";
  private static final String ACTION_PLAN_NAME = "action plan 1";
  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action Statetransition failed.";
  private static final String NOTIFY = "Notify";

  private static final UUID ACTION_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d1");
  private static final UUID CASE_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");

  @Spy private AppConfig appConfig = new AppConfig();

  @Mock private ActionStateService actionStateService;

  @Mock private ActionRequestContextFactory decoratorContextFactory;

  @Mock private NotifyService notifyServiceMock;
  @Mock private NotificationFileCreator notificationFileCreator;
  @Mock private ActionCaseRepository actionCaseRepo;

  @InjectMocks private ActionProcessingService businessActionProcessingService;

  private CaseDetailsDTO hCase;
  private CaseDetailsDTO bCase;
  private List<CollectionExerciseDTO> collectionExercises;
  private PartyDTO businessParty;
  private List<PartyDTO> respondentParties;
  private List<SurveyDTO> surveys;

  private ActionRequestContext context;
  private Action contextAction;
  private ActionType contextActionType;
  private List<Action> contextActions;

  private ActionCase actionCase;

  /** Initialises Mockito and loads Class Fixtures */
  @Before
  public void setUp() throws Exception {
    final CaseSvc caseSvcConfig = new CaseSvc();
    appConfig.setCaseSvc(caseSvcConfig);

    // Load test data
    List<PartyDTO> partys = FixtureHelper.loadClassFixtures(PartyDTO[].class);
    businessParty = partys.get(0);
    respondentParties = partys.subList(1, 4);

    List<CaseDetailsDTO> caseDetails = FixtureHelper.loadClassFixtures(CaseDetailsDTO[].class);
    hCase = caseDetails.get(0);
    bCase = caseDetails.get(1);
    collectionExercises = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    surveys = FixtureHelper.loadClassFixtures(SurveyDTO[].class);

    actionCase = new ActionCase();
    actionCase.setSampleUnitType("B");

    // Set up context
    context = createContext();
    contextActions = new ArrayList<>();
    contextActions.add(context.getAction());
    contextActionType = context.getAction().getActionType();

    MockitoAnnotations.initMocks(this);

    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
        .thenReturn(context);
  }

  private ActionRequestContext createContext() {
    context = new ActionRequestContext();
    ActionPlan contextActionPlan =
        ActionPlan.builder().name(ACTION_PLAN_NAME).id(UUID.randomUUID()).build();
    context.setActionPlan(contextActionPlan);
    context.setCaseDetails(bCase);
    context.setParentParty(businessParty);
    context.setCollectionExercise(collectionExercises.get(0));
    context.setSurvey(surveys.get(0));
    context.setSampleUnitType(SampleUnitType.B);
    context.setAction(createContextAction(PRINTER));
    return context;
  }

  private Action createContextAction(String handler) {
    contextAction = new Action();
    contextAction.setId(ACTION_ID);
    contextAction.setActionType(
        ActionType.builder()
            .responseRequired(Boolean.TRUE)
            .handler(handler)
            .actionTypePK(1)
            .build());
    contextAction.setActionPlanFK(ACTION_PLAN_FK);
    contextAction.setCaseId(CASE_ID);
    contextAction.setPriority(1);
    return contextAction;
  }

  /** Happy path for processing an B case business action */
  @Test
  public void testProcessActionRequestBCaseBusiness() throws CTPException {

    // Given setUp()
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    businessActionProcessingService.processActions(contextActionType, contextActions);

    // Then
    verify(notificationFileCreator, times(1)).export(any(), any());
  }

  /** Happy path for processing an B case respondent action */
  @Test
  public void testProcessActionRequestBCaseRespondentsNotification() throws CTPException {
    Action notifyAction = createContextAction(NOTIFY);
    List<Action> actions = new ArrayList<>();
    actions.add(notifyAction);

    // Given
    ActionRequestContext requestContext = createContext();
    requestContext.setAction(notifyAction);

    requestContext.setChildParties(respondentParties);
    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
        .thenReturn(requestContext);
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    businessActionProcessingService.processActions(notifyAction.getActionType(), actions);

    // Then
    verify(actionStateService, times(1))
        .transitionAction(any(Action.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(notifyServiceMock, times(3)).processNotification(any(ActionRequest.class));
    verify(notificationFileCreator, never()).export(any(), any());
  }

  @Test(expected = IllegalStateException.class)
  public void testProcessActionRequestNoActionType() throws CTPException {

    // Given
    Action action = new Action();
    List<Action> actions = new ArrayList<>();
    actions.add(action);
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    businessActionProcessingService.processActions(null, actions);

    // Then
    verify(actionStateService, never())
        .transitionAction(any(Action.class), any(ActionDTO.ActionEvent.class));
    verify(notificationFileCreator, never()).export(any(), any());
  }

  @Test
  public void testProcessActionRequestActionTypeWithNoResponseRequired() throws CTPException {
    Action action = new Action();
    action.setActionType(ActionType.builder().build());

    List<Action> actions = new ArrayList<>();
    actions.add(action);
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // Given
    businessActionProcessingService.processActions(action.getActionType(), actions);

    // Then
    verify(actionStateService, never())
        .transitionAction(any(Action.class), any(ActionDTO.ActionEvent.class));
    verify(notificationFileCreator, never()).export(any(), any());
  }

  /** Scenario where actionSvcStateTransitionManager throws an exception on transition */
  @Test(expected = IllegalStateException.class)
  public void testProcessActionCancelStateTransitionException() throws CTPException {

    UUID newActionId = UUID.randomUUID();
    final Action action = new Action();
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(PRINTER).build());
    action.setState(ActionDTO.ActionState.CANCEL_SUBMITTED);
    action.setId(newActionId);
    List<Action> actions = new ArrayList<>();
    actions.add(action);

    // Given
    doThrow(new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG))
        .when(actionStateService)
        .transitionAction(any(Action.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));

    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    businessActionProcessingService.processActions(action.getActionType(), actions);

    // Then
    verify(actionStateService, times(1))
        .transitionAction(any(Action.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(notificationFileCreator, never()).export(any(), any());
  }

  /** Scenario where the action's state transitions OK but issue while persisting action to DB */
  @Test(expected = RuntimeException.class)
  public void testProcessActionCancelPersistingException() throws CTPException {
    Action action = new Action();
    List<Action> actions = new ArrayList<>();
    actions.add(action);

    // Given
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    businessActionProcessingService.processActions(action.getActionType(), actions);

    // Then
    verify(actionStateService, times(1))
        .transitionAction(any(Action.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(notificationFileCreator, never()).export(any(), any());
  }

  @Test
  public void testProcessActionCancelHappyPath() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    final Action action = new Action();
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(PRINTER).build());
    action.setState(ActionDTO.ActionState.CANCEL_SUBMITTED);
    action.setId(newActionId);
    List<Action> actions = new ArrayList<>();
    actions.add(action);

    // Given
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    businessActionProcessingService.processActions(action.getActionType(), actions);

    // Then
    verify(actionStateService, times(1))
        .transitionAction(any(Action.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
  }

  @Test
  public void testCaseRefShouldBeOnActionRequest() throws CTPException {
    // Given
    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
        .thenReturn(context);
    when(actionCaseRepo.findById(any())).thenReturn(actionCase);

    // When
    List<ActionRequest> requests =
        businessActionProcessingService.prepareActionRequests(contextAction);

    // Then
    assertEquals(context.getCaseDetails().getCaseRef(), requests.get(0).getCaseRef());
  }
}
