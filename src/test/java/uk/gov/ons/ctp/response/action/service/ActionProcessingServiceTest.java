// package uk.gov.ons.ctp.response.action.service;
//
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertTrue;
// import static org.junit.Assert.fail;
// import static org.mockito.Matchers.any;
// import static org.mockito.Mockito.eq;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import static uk.gov.ons.ctp.response.action.service.ActionProcessingService.CANCELLATION_REASON;
//
// import java.util.Collections;
// import java.util.List;
// import java.util.UUID;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.Spy;
// import org.mockito.runners.MockitoJUnitRunner;
// import uk.gov.ons.ctp.common.FixtureHelper;
// import uk.gov.ons.ctp.common.error.CTPException;
// import uk.gov.ons.ctp.common.state.StateTransitionManager;
// import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
// import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
// import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
// import uk.gov.ons.ctp.response.action.client.SurveySvcClientService;
// import uk.gov.ons.ctp.response.action.config.AppConfig;
// import uk.gov.ons.ctp.response.action.config.CaseSvc;
// import uk.gov.ons.ctp.response.action.domain.model.Action;
// import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
// import uk.gov.ons.ctp.response.action.domain.model.ActionType;
// import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
// import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
// import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
// import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
// import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
// import uk.gov.ons.ctp.response.action.representation.ActionDTO;
// import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
// import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
// import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
// import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
// import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
// import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
// import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
// import uk.gov.ons.ctp.response.party.representation.Attributes;
// import uk.gov.ons.ctp.response.party.representation.PartyDTO;
// import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
// import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;
// import uk.gov.ons.response.survey.representation.SurveyDTO;
//
/// ** Tests for the ActionProcessingServiceImpl */
// @RunWith(MockitoJUnitRunner.class)
// public class ActionProcessingServiceTest {
//
//  private static final Integer ACTION_PLAN_FK = 1;
//
//  private static final String ACTIONEXPORTER = "actionExporter";
//  private static final String ACTION_PLAN_NAME = "action plan 1";
//  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action State transition
// failed.";
//  private static final String CENSUS_SURVEY_ID = "Census2021";
//  private static final String DB_ERROR_MSG = "DB is KO.";
//  private static final String REST_ERROR_MSG = "REST call is KO.";
//  private static final String SAMPLE_UNIT_TYPE_H = "H";
//  private static final String SAMPLE_UNIT_TYPE_HI = "HI";
//
//  private static final UUID ACTION_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d1");
//  private static final UUID CASE_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");
//  private static final UUID CASE_ID_1 = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d5");
//  private static final UUID CASE_ID_2 = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d6");
//  private static final UUID COLLECTION_EXERCISE_ID =
//      UUID.fromString("c2124abc-10c6-4c7c-885a-779d185a03a4");
//  private static final UUID PARTY_ID = UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c86");
//  private static final UUID PARTY_ID_PARENT_FOR_CASE_ID_2 =
//      UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c81");
//
//  @Spy private AppConfig appConfig = new AppConfig();
//
//  @Mock private CaseSvcClientService caseSvcClientService;
//
//  @Mock private CollectionExerciseClientService collectionExerciseClientService;
//
//  @Mock private PartySvcClientService partySvcClientService;
//
//  @Mock private SurveySvcClientService surveySvcClientService;
//
//  @Mock private ActionRepository actionRepo;
//
//  @Mock private ActionPlanRepository actionPlanRepo;
//
//  @Mock private ActionInstructionPublisher actionInstructionPublisher;
//
//  @Mock
//  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
//      actionSvcStateTransitionManager;
//
//  @Mock private ActionRequestValidator validator;
//
//  @Mock private ActionRequestContextFactory decoratorContextFactory;
//
//  @InjectMocks private BusinessActionProcessingService businessActionProcessingService;
//
//  private List<CaseDetailsDTO> caseDetailsDTOs;
//  private List<CollectionExerciseDTO> collectionExerciseDTOs;
//  private List<PartyDTO> partyDTOs;
//  private List<SurveyDTO> surveyDTOs;
//
//  /** Initialises Mockito and loads Class Fixtures */
//  @Before
//  public void setUp() throws Exception {
//    final CaseSvc caseSvcConfig = new CaseSvc();
//    appConfig.setCaseSvc(caseSvcConfig);
//
//    partyDTOs = FixtureHelper.loadClassFixtures(PartyDTO[].class);
//    caseDetailsDTOs = FixtureHelper.loadClassFixtures(CaseDetailsDTO[].class);
//    collectionExerciseDTOs = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
//    surveyDTOs = FixtureHelper.loadClassFixtures(SurveyDTO[].class);
//
//    MockitoAnnotations.initMocks(this);
//  }
//
//  @Test
//  public void testProcessActionRequestNoActionType() throws CTPException {
//    final Action action = new Action();
//    businessActionProcessingService.processActionRequest(action);
//
//    verify(actionSvcStateTransitionManager, never())
//        .transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
//    verify(actionRepo, never()).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, never())
//        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
//    verify(actionInstructionPublisher, never())
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  @Test
//  public void testProcessActionRequestActionTypeWithNoResponseRequired() throws CTPException {
//    final Action action = new Action();
//    action.setActionType(ActionType.builder().build());
//    businessActionProcessingService.processActionRequest(action);
//
//    verify(actionSvcStateTransitionManager, never())
//        .transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
//    verify(actionRepo, never()).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, never())
//        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
//    verify(actionInstructionPublisher, never())
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /** An exception is thrown when transitioning the state of the Action */
//  @Test
//  public void testProcessActionRequestActionStateTransitionThrowsException() throws CTPException {
//    when(actionSvcStateTransitionManager.transition(
//            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
//        .thenThrow(
//            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));
//
//    final Action action = new Action();
//    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
//    try {
//      businessActionProcessingService.processActionRequest(action);
//      fail();
//    } catch (final CTPException e) {
//      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
//      assertEquals(ACTION_STATE_TRANSITION_ERROR_MSG, e.getMessage());
//    }
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
//    verify(actionRepo, never()).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, never())
//        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
//    verify(actionInstructionPublisher, never())
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /** An exception is thrown when saving the Action after its state was transitioned */
//  @Test
//  public void testProcessActionRequestActionPersistingActionThrowsException() throws CTPException
// {
//    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new
// RuntimeException(DB_ERROR_MSG));
//
//    final Action action = new Action();
//    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
//    try {
//      businessActionProcessingService.processActionRequest(action);
//      fail();
//    } catch (final RuntimeException e) {
//      assertEquals(DB_ERROR_MSG, e.getMessage());
//    }
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, never())
//        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
//    verify(actionInstructionPublisher, never())
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /**
//   * An exception is thrown when creating a CaseEvent after the Action's state was transitioned
// and
//   * persisted OK.
//   */
//  @Test
//  public void testProcessActionRequestCaseEventCreationThrowsException() throws CTPException {
//    // Start of section to mock responses
//
//    ActionRequestContext context = new ActionRequestContext();
//
//    final ActionPlan actionPlan =
//        ActionPlan.builder().name(ACTION_PLAN_NAME).id(UUID.randomUUID()).build();
//    context.setActionPlan(actionPlan);
//    context.setCaseDetails(caseDetailsDTOs.get(0));
//    context.setCollectionExercise(collectionExerciseDTOs.get(0));
//    context.setSurvey(surveyDTOs.get(0));
//    context.setParentParty(partyDTOs.get(0));
//    context.setSampleUnitType(SampleUnitType.B);
//
//    when(caseSvcClientService.createNewCaseEvent(
//            any(Action.class), any(CategoryDTO.CategoryName.class)))
//        .thenThrow(new RuntimeException(REST_ERROR_MSG));
//
//    when(actionSvcStateTransitionManager.transition(
//            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
//        .thenReturn(ActionDTO.ActionState.PENDING);
//    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
//    // End of section to mock responses
//
//    try {
//      final Action action = new Action();
//      action.setId(ACTION_ID);
//      action.setActionType(
//          ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
//      action.setActionPlanFK(ACTION_PLAN_FK);
//      action.setCaseId(CASE_ID);
//      action.setPriority(1);
//      context.setAction(action);
//
//      when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
//          .thenReturn(context);
//
//      businessActionProcessingService.processActionRequest(action);
//      fail();
//    } catch (final RuntimeException e) {
//      assertEquals(REST_ERROR_MSG, e.getMessage());
//    }
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    verify(partySvcClientService, never()).getParty(eq(SAMPLE_UNIT_TYPE_HI), any(UUID.class));
//    verify(actionInstructionPublisher, times(1))
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//    verify(caseSvcClientService, times(1))
//        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
//  }
//
//  /**
//   * Happy path for an action linked to a case for a PARENT sample unit (a H one), ie we go all
// the
//   * way to producing an ActionRequest and publishing it.
//   */
//  @Test
//  public void testProcessActionRequestHappyPathParentUnit() throws CTPException {
//    // Start of section to mock responses
//    ActionRequestContext context = new ActionRequestContext();
//
//    final ActionPlan actionPlan =
//        ActionPlan.builder().name(ACTION_PLAN_NAME).id(UUID.randomUUID()).build();
//    context.setActionPlan(actionPlan);
//    context.setCaseDetails(caseDetailsDTOs.get(0));
//    context.setParentParty(partyDTOs.get(0));
//    context.setCollectionExercise(collectionExerciseDTOs.get(0));
//    context.setSurvey(surveyDTOs.get(0));
//    context.setSampleUnitType(SampleUnitType.B);
//
//    when(actionSvcStateTransitionManager.transition(
//            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
//        .thenReturn(ActionDTO.ActionState.PENDING);
//    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
//    // End of section to mock responses
//
//    // Start of section to run the test
//    final Action action = new Action();
//    action.setId(ACTION_ID);
//    action.setActionType(
//        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
//    action.setActionPlanFK(ACTION_PLAN_FK);
//    action.setCaseId(CASE_ID);
//    action.setPriority(1);
//    context.setAction(action);
//
//    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
//        .thenReturn(context);
//
//    businessActionProcessingService.processActionRequest(action);
//    // End of section to run the test
//
//    // Start of section to verify calls
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, times(1))
//        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
//    // TODO Be more specific on the Action below once CTPA-1390 has been discussed & implemented
//    verify(actionInstructionPublisher, times(1))
//        .sendActionInstruction(
//            eq(ACTIONEXPORTER),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /**
//   * Happy path for an action linked to a case for a CHILD sample unit (a HI one), ie we go all
// the
//   * way to producing an ActionRequest and publishing it.
//   */
//  @Test
//  public void testProcessActionRequestHappyPathChildUnit() throws CTPException {
//    // Start of section to mock responses
//    ActionRequestContext context = new ActionRequestContext();
//
//    final ActionPlan actionPlan =
//        ActionPlan.builder().name(ACTION_PLAN_NAME).id(UUID.randomUUID()).build();
//    context.setActionPlan(actionPlan);
//    context.setCaseDetails(caseDetailsDTOs.get(2));
//    context.setParentParty(partyDTOs.get(0));
//    context.setCollectionExercise(collectionExerciseDTOs.get(0));
//    context.setSurvey(surveyDTOs.get(0));
//    context.setChildParties(Collections.singletonList(partyDTOs.get(1)));
//    context.setSampleUnitType(SampleUnitType.HI);
//
//    when(actionSvcStateTransitionManager.transition(
//            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
//        .thenReturn(ActionDTO.ActionState.PENDING);
//    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
//    // End of section to mock responses
//
//    // Start of section to run the test
//    final Action action = new Action();
//    action.setId(ACTION_ID);
//    action.setActionType(
//        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
//    action.setActionPlanFK(ACTION_PLAN_FK);
//    action.setCaseId(CASE_ID_2);
//    action.setPriority(1);
//    context.setAction(action);
//
//    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
//        .thenReturn(context);
//
//    businessActionProcessingService.processActionRequest(action);
//    // End of section to run the test
//
//    // Start of section to verify calls
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, times(1))
//        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
//    // TODO Be more specific on the Action below once CTPA-1390 has been discussed & implemented
//    verify(actionInstructionPublisher, times(1))
//        .sendActionInstruction(
//            eq(ACTIONEXPORTER),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /** Scenario where actionSvcStateTransitionManager throws an exception on transition */
//  @Test
//  public void testProcessActionCancelStateTransitionException() throws CTPException {
//    when(actionSvcStateTransitionManager.transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED)))
//        .thenThrow(
//            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));
//
//    try {
//      final Action action = new Action();
//      businessActionProcessingService.processActionCancel(action);
//      fail();
//    } catch (final CTPException e) {
//      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
//      assertEquals(ACTION_STATE_TRANSITION_ERROR_MSG, e.getMessage());
//    }
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
//    verify(actionRepo, never()).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, never())
//        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
//    verify(actionInstructionPublisher, never())
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /** Scenario where the action's state transitions OK but issue while persisting action to DB */
//  @Test
//  public void testProcessActionCancelPersistingException() throws CTPException {
//    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new
// RuntimeException(DB_ERROR_MSG));
//
//    try {
//      final Action action = new Action();
//      businessActionProcessingService.processActionCancel(action);
//      fail();
//    } catch (final RuntimeException e) {
//      assertEquals(DB_ERROR_MSG, e.getMessage());
//    }
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, never())
//        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
//    verify(actionInstructionPublisher, never())
//        .sendActionInstruction(
//            any(String.class),
//            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
//  }
//
//  /**
//   * An exception is thrown when creating a CaseEvent after the Action's state was transitioned
// and
//   * persisted OK.
//   */
//  @Test
//  public void testProcessActionCancelCaseEventCreationThrowsException() throws CTPException {
//    when(caseSvcClientService.createNewCaseEvent(
//            any(Action.class), any(CategoryDTO.CategoryName.class)))
//        .thenThrow(new RuntimeException(REST_ERROR_MSG));
//
//    try {
//      final Action action = new Action();
//      action.setActionType(
//          ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
//      action.setId(ACTION_ID);
//      businessActionProcessingService.processActionCancel(action);
//      fail();
//    } catch (final RuntimeException e) {
//      assertEquals(REST_ERROR_MSG, e.getMessage());
//    }
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    final ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
//        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
//    verify(actionInstructionPublisher, times(1))
//        .sendActionInstruction(eq(ACTIONEXPORTER), actionCaptor.capture());
//    final uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel =
//        (ActionCancel) actionCaptor.getValue();
//    assertEquals(ACTION_ID.toString(), publishedActionCancel.getActionId());
//    assertTrue(publishedActionCancel.isResponseRequired());
//    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
//    verify(caseSvcClientService, times(1))
//        .createNewCaseEvent(
//            any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED));
//  }
//
//  @Test
//  public void testProcessActionCancelHappyPath() throws CTPException {
//    final Action action = new Action();
//    action.setActionType(
//        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
//    action.setId(ACTION_ID);
//    businessActionProcessingService.processActionCancel(action);
//
//    verify(actionSvcStateTransitionManager, times(1))
//        .transition(
//            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
//    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
//    verify(caseSvcClientService, times(1))
//        .createNewCaseEvent(
//            any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED));
//
//    final ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
//        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
//    verify(actionInstructionPublisher, times(1))
//        .sendActionInstruction(eq(ACTIONEXPORTER), actionCaptor.capture());
//    final uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel =
//        (ActionCancel) actionCaptor.getValue();
//    assertEquals(ACTION_ID.toString(), publishedActionCancel.getActionId());
//    assertTrue(publishedActionCancel.isResponseRequired());
//    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
//  }
//
//  @Test
//  public void testCaseRefShouldBeOnActionRequest() throws CTPException {
//    // Given
//    CaseDetailsDTO caseDetails = createCaseDetails();
//    caseDetails.setCaseRef("Case ref");
//    ActionRequestContext context = new ActionRequestContext();
//
//    context.setCaseDetails(caseDetails);
//    context.setCollectionExercise(createCollectionExercise());
//    context.setSurvey(new SurveyDTO());
//    context.setParentParty(createParty());
//
//    final ActionPlan actionPlan =
//        ActionPlan.builder().name(ACTION_PLAN_NAME).id(UUID.randomUUID()).build();
//    context.setActionPlan(actionPlan);
//    context.setSampleUnitType(SampleUnitType.B);
//
//    final Action action = new Action();
//    action.setId(ACTION_ID);
//    action.setActionType(
//        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
//    action.setCaseId(CASE_ID);
//    action.setPriority(1);
//    context.setAction(action);
//
//    when(this.decoratorContextFactory.getActionRequestDecoratorContext(any(Action.class)))
//        .thenReturn(context);
//
//    // When
//    businessActionProcessingService.processActionRequest(action);
//
//    // Then
//    ArgumentCaptor<ActionRequest> captor = ArgumentCaptor.forClass(ActionRequest.class);
//    verify(actionInstructionPublisher).sendActionInstruction(eq(ACTIONEXPORTER),
// captor.capture());
//    assertEquals("Case ref", captor.getValue().getCaseRef());
//  }
//
//  private CollectionExerciseDTO createCollectionExercise() {
//    CollectionExerciseDTO collectionExercise = new CollectionExerciseDTO();
//    collectionExercise.setSurveyId(CENSUS_SURVEY_ID);
//    return collectionExercise;
//  }
//
//  private CaseDetailsDTO createCaseDetails() {
//    CaseDetailsDTO caseDetails = new CaseDetailsDTO();
//    caseDetails.setCaseEvents(Collections.emptyList());
//    caseDetails.setSampleUnitType(SampleUnitDTO.SampleUnitType.H.toString());
//    caseDetails.setCaseGroup(createCaseGroup());
//    caseDetails.setPartyId(PARTY_ID);
//    caseDetails.setId(UUID.randomUUID());
//    return caseDetails;
//  }
//
//  private CaseGroupDTO createCaseGroup() {
//    CaseGroupDTO caseGroup = new CaseGroupDTO();
//    caseGroup.setCaseGroupStatus(CaseGroupStatus.INPROGRESS);
//    caseGroup.setCollectionExerciseId(COLLECTION_EXERCISE_ID);
//    return caseGroup;
//  }
//
//  private PartyDTO createParty() {
//    PartyDTO party = new PartyDTO();
//    party.setAttributes(new Attributes());
//    party.setAssociations(Collections.emptyList());
//    return party;
//  }
// }
