package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.action.service.ActionProcessingService.CANCELLATION_REASON;

import java.util.List;
import java.util.UUID;
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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.CaseSvc;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;
import uk.gov.ons.response.survey.representation.SurveyDTO;

/** Tests for the ActionProcessingServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionProcessingServiceTest {

  private static final Integer ACTION_PLAN_FK = 1;

  private static final String PRINTER = "Printer";
  private static final String ACTION_PLAN_NAME = "action plan 1";
  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action Statetransitionfailed.";
  private static final String DB_ERROR_MSG = "DB is KO.";
  private static final String NOTIFY = "Notify";

  private static final UUID ACTION_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d1");
  private static final UUID CASE_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");

  @Spy private AppConfig appConfig = new AppConfig();

  @InjectMocks private BusinessActionProcessingService businessActionProcessingService;

  @Mock
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  @Mock private ActionInstructionPublisher actionInstructionPublisher;
  @Mock private ActionRequestValidator validator;
  @Mock private ActionRequestContextFactory decoratorContextFactory;

  @Mock private ActionRepository actionRepo;
  @Mock private ActionPlanRepository actionPlanRepo;

  private CaseDetailsDTO hCase;
  private CaseDetailsDTO bCase;
  private List<CollectionExerciseDTO> collectionExercises;
  private PartyDTO businessParty;
  private List<PartyDTO> respondentParties;
  private List<SurveyDTO> surveys;

  private ActionRequestContext context;
  private Action contextAction;

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

    // Set up context
    context = createContext();

    MockitoAnnotations.initMocks(this);

    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenReturn(ActionDTO.ActionState.PENDING);
    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
        .thenReturn(context);
    when(actionRepo.findById(eq(ACTION_ID))).thenReturn(contextAction);
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

    // When
    businessActionProcessingService.processActionRequests(ACTION_ID);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(
            eq(PRINTER), any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** Happy path for processing an B case respondent action */
  @Test
  public void testProcessActionRequestBCaseRespondentsNotification() throws CTPException {
    Action notifyAction = createContextAction(NOTIFY);
    when(actionRepo.findById(eq(ACTION_ID))).thenReturn(notifyAction);

    // Given
    context.setAction(notifyAction);
    context.setChildParties(respondentParties);
    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
        .thenReturn(context);

    // When
    businessActionProcessingService.processActionRequests(ACTION_ID);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, times(3))
        .sendActionInstruction(
            eq(NOTIFY), any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  @Test(expected = IllegalStateException.class)
  public void testProcessActionRequestNoActionType() throws CTPException {
    UUID newActionId = UUID.randomUUID();

    // Given
    when(actionRepo.findById(eq(newActionId))).thenReturn(new Action());

    // When
    businessActionProcessingService.processActionRequests(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, never())
        .transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  @Test(expected = IllegalStateException.class)
  public void testProcessActionRequestActionTypeWithNoResponseRequired() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    Action action = new Action();
    action.setActionType(ActionType.builder().build());

    // Given/When
    when(actionRepo.findById(eq(newActionId))).thenReturn(action);

    // Given
    businessActionProcessingService.processActionRequests(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, never())
        .transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** An exception is thrown when transitioning the state of the Action */
  @Test(expected = IllegalStateException.class)
  public void testProcessActionRequestActionStateTransitionThrowsException() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());

    // Given
    when(actionRepo.findById(eq(newActionId))).thenReturn(action);
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenThrow(
            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));

    // When
    businessActionProcessingService.processActionRequests(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** An exception is thrown when saving the Action after its state was transitioned */
  @Test(expected = RuntimeException.class)
  public void testProcessActionRequestActionPersistingActionThrowsException() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());

    // Given
    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new RuntimeException(DB_ERROR_MSG));

    // When
    businessActionProcessingService.processActionRequests(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** Scenario where actionSvcStateTransitionManager throws an exception on transition */
  @Test(expected = IllegalStateException.class)
  public void testProcessActionCancelStateTransitionException() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    Action action = new Action();

    // Given
    when(actionRepo.findById(eq(newActionId))).thenReturn(action);
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED)))
        .thenThrow(
            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));

    // When
    businessActionProcessingService.processActionCancel(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** Scenario where the action's state transitions OK but issue while persisting action to DB */
  @Test(expected = RuntimeException.class)
  public void testProcessActionCancelPersistingException() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    Action action = new Action();

    // Given
    when(actionRepo.findById(eq(newActionId))).thenReturn(action);
    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new RuntimeException(DB_ERROR_MSG));

    // When
    businessActionProcessingService.processActionCancel(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  @Test
  public void testProcessActionCancelHappyPath() throws CTPException {
    UUID newActionId = UUID.randomUUID();
    final Action action = new Action();
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(PRINTER).build());
    action.setId(newActionId);

    // Given
    when(actionRepo.findById(eq(newActionId))).thenReturn(action);

    // When
    businessActionProcessingService.processActionCancel(newActionId);

    // Then
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));

    final ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(eq(PRINTER), actionCaptor.capture());
    final uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel =
        (ActionCancel) actionCaptor.getValue();
    assertEquals(newActionId.toString(), publishedActionCancel.getActionId());
    assertTrue(publishedActionCancel.isResponseRequired());
    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
  }

  @Test
  public void testCaseRefShouldBeOnActionRequest() throws CTPException {
    // Given
    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
        .thenReturn(context);

    // When
    businessActionProcessingService.processActionRequests(ACTION_ID);

    // Then
    ArgumentCaptor<ActionRequest> captor = ArgumentCaptor.forClass(ActionRequest.class);
    verify(actionInstructionPublisher).sendActionInstruction(eq(PRINTER), captor.capture());
    assertEquals(context.getCaseDetails().getCaseRef(), captor.getValue().getCaseRef());
  }
}
