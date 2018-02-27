package uk.gov.ons.ctp.response.action.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
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
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.Attributes;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.ons.ctp.response.action.service.impl.ActionProcessingServiceImpl.CANCELLATION_REASON;

/**
 * Tests for the ActionProcessingServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionProcessingServiceImplTest {

  private static final Integer ACTION_PLAN_FK = 1;

  private static final String ACTIONEXPORTER = "actionExporter";
  private static final String ACTION_PLAN_NAME = "action plan 1";
  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action State transition failed.";
  private static final String CENSUS = "Census2021";
  private static final String DB_ERROR_MSG = "DB is KO.";
  private static final String REST_ERROR_MSG = "REST call is KO.";
  private static final String SAMPLE_UNIT_TYPE_H = "H";
  private static final String SAMPLE_UNIT_TYPE_HI = "HI";
  private static final Integer B_PARTY = 4;
  private static final Integer ACTIVE_BI = 5;
  private static final Integer SUSPENDED_BI = 6;
  private static final Integer CREATED_BI = 7;
  private static final Integer NO_ASSOCIATIONS_BI = 8;


  private static final UUID ACTION_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d1");
  private static final UUID CASE_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");
  private static final UUID CASE_ID_1 = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d5");
  private static final UUID CASE_ID_2 = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d6");
  private static final UUID COLLECTION_EXERCISE_ID = UUID.fromString("c2124abc-10c6-4c7c-885a-779d185a03a4");
  private static final UUID PARTY_ID = UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c86");
  private static final UUID PARTY_ID_PARENT_FOR_CASE_ID_2 = UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c81");

  @Spy
  private AppConfig appConfig = new AppConfig();

  @Mock
  private CaseSvcClientService caseSvcClientService;

  @Mock
  private CollectionExerciseClientService collectionExerciseClientService;

  @Mock
  private PartySvcClientService partySvcClientService;

  @Mock
  private SurveySvcClientService surveySvcClientService;

  @Mock
  private ActionRepository actionRepo;

  @Mock
  private ActionPlanRepository actionPlanRepo;

  @Mock
  private ActionInstructionPublisher actionInstructionPublisher;

  @Mock
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent> actionSvcStateTransitionManager;

  @Mock
  private ActionRequestValidator validator;

  @InjectMocks
  private ActionProcessingServiceImpl actionProcessingService;

  private List<CaseDetailsDTO> caseDetailsDTOs;
  private List<CollectionExerciseDTO> collectionExerciseDTOs;
  private List<PartyDTO> partyDTOs;
  private List<SurveyDTO> surveyDTOs;

  /**
   * Initialises Mockito and loads Class Fixtures
   */
  @Before
  public void setUp() throws Exception {
    CaseSvc caseSvcConfig = new CaseSvc();
    appConfig.setCaseSvc(caseSvcConfig);

    partyDTOs = FixtureHelper.loadClassFixtures(PartyDTO[].class);
    caseDetailsDTOs = FixtureHelper.loadClassFixtures(CaseDetailsDTO[].class);
    collectionExerciseDTOs = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    surveyDTOs = FixtureHelper.loadClassFixtures(SurveyDTO[].class);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testProcessActionRequestNoActionType() throws CTPException {
    Action action = new Action();
    actionProcessingService.processActionRequest(action);

    verify(actionSvcStateTransitionManager, never()).transition(any(ActionDTO.ActionState.class),
        any(ActionDTO.ActionEvent.class));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never()).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  @Test
  public void testProcessActionRequestActionTypeWithNoResponseRequired() throws CTPException {
    Action action = new Action();
    action.setActionType(ActionType.builder().build());
    actionProcessingService.processActionRequest(action);

    verify(actionSvcStateTransitionManager, never()).transition(any(ActionDTO.ActionState.class),
        any(ActionDTO.ActionEvent.class));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never()).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * An exception is thrown when transitioning the state of the Action
   */
  @Test
  public void testProcessActionRequestActionStateTransitionThrowsException() throws CTPException {
    when(actionSvcStateTransitionManager.transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenThrow(new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));

    Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
    try{
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ACTION_STATE_TRANSITION_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never()).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * An exception is thrown when saving the Action after its state was transitioned
   */
  @Test
  public void testProcessActionRequestActionPersistingActionThrowsException() throws CTPException {
    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new RuntimeException(DB_ERROR_MSG));

    Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
    try {
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (RuntimeException e) {
      assertEquals(DB_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never()).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * An exception is thrown when creating a CaseEvent after the Action's state was transitioned and persisted OK.
   */
  @Test
  public void testProcessActionRequestCaseEventCreationThrowsException() throws CTPException {
    // Start of section to mock responses
    ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID)).thenReturn(caseDetailsDTOs.get(0));

    when(partySvcClientService.getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID)).thenReturn(partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID)).
        thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS)).thenReturn(surveyDTOs.get(0));

    when(caseSvcClientService.createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class))).
        thenThrow(new RuntimeException(REST_ERROR_MSG));

    when(actionSvcStateTransitionManager.transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class))).thenReturn(ActionDTO.ActionState.PENDING);
      when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    // End of section to mock responses

    try {
      Action action = new Action();
      action.setId(ACTION_ID);
      action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
      action.setActionPlanFK(ACTION_PLAN_FK);
      action.setCaseId(CASE_ID);
      action.setPriority(1);
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (RuntimeException e) {
      assertEquals(REST_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID);
    verify(partySvcClientService, times(1)).getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID);
    verify(partySvcClientService, never()).getParty(eq(SAMPLE_UNIT_TYPE_HI), any(UUID.class));
    verify(collectionExerciseClientService, times(1)).
        getCollectionExercise(COLLECTION_EXERCISE_ID);
    verify(surveySvcClientService, times(1)).requestDetailsForSurvey(CENSUS);
    verify(actionInstructionPublisher, times(1)).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        eq(CategoryDTO.CategoryName.ACTION_CREATED));
  }

  /**
   * Happy path for an action linked to a case for a PARENT sample unit (a H one), ie we go all the way to producing
   * an ActionRequest and publishing it.
   */
  @Test
  public void testProcessActionRequestHappyPathParentUnit() throws CTPException {
    // Start of section to mock responses
    ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID)).thenReturn(caseDetailsDTOs.get(0));

    when(partySvcClientService.getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID)).thenReturn(partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID)).
        thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS)).thenReturn(surveyDTOs.get(0));

    when(actionSvcStateTransitionManager.transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class))).thenReturn(ActionDTO.ActionState.PENDING);
    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    // End of section to mock responses

    // Start of section to run the test
    Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setActionPlanFK(ACTION_PLAN_FK);
    action.setCaseId(CASE_ID);
    action.setPriority(1);
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // Start of section to verify calls
    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        eq(CategoryDTO.CategoryName.ACTION_CREATED));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID);
    verify(partySvcClientService, times(1)).getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID);
    verify(partySvcClientService, never()).getParty(eq(SAMPLE_UNIT_TYPE_HI), any(UUID.class));
    verify(collectionExerciseClientService, times(1)).
        getCollectionExercise(COLLECTION_EXERCISE_ID);
    verify(surveySvcClientService, times(1)).requestDetailsForSurvey(CENSUS);
    // TODO Be more specific on the Action below once CTPA-1390 has been discussed & implemented
    verify(actionInstructionPublisher, times(1)).sendActionInstruction(eq(ACTIONEXPORTER),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Test where an action is linked to a case for a sample unit which is not recognised (ie none of the expected values
   * H, HI, CI, B, BI)
   */
  @Test
  public void testProcessActionRequestForActionLinkedToInvalidSampleUnitType() throws CTPException {
    // Start of section to mock responses
    ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID_1)).thenReturn(caseDetailsDTOs.get(1)); // the returned case has a sample unit type of Z
    // End of section to mock responses

    // Start of section to run the test
    Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setActionPlanFK(ACTION_PLAN_FK);
    action.setCaseId(CASE_ID_1);
    action.setPriority(1);
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // Start of section to verify calls
    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        eq(CategoryDTO.CategoryName.ACTION_CREATED));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID_1);
    verify(partySvcClientService, never()).getParty(any(String.class), any(UUID.class));
    verify(collectionExerciseClientService, never()).getCollectionExercise(any(UUID.class));
    verify(surveySvcClientService, never()).requestDetailsForSurvey(any(String.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Happy path for an action linked to a case for a CHILD sample unit (a HI one), ie we go all the way to producing
   * an ActionRequest and publishing it.
   */
  @Test
  public void testProcessActionRequestHappyPathChildUnit() throws CTPException {
    // Start of section to mock responses
    ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID_2)).thenReturn(caseDetailsDTOs.get(2));

    when(partySvcClientService.getParty(SAMPLE_UNIT_TYPE_HI, PARTY_ID)).thenReturn(partyDTOs.get(1));
    when(partySvcClientService.getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID_PARENT_FOR_CASE_ID_2)).thenReturn(
        partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID)).
        thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS)).thenReturn(surveyDTOs.get(0));
    when(actionSvcStateTransitionManager.transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class))).thenReturn(ActionDTO.ActionState.PENDING);
    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    // End of section to mock responses

    // Start of section to run the test
    Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setActionPlanFK(ACTION_PLAN_FK);
    action.setCaseId(CASE_ID_2);
    action.setPriority(1);
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // Start of section to verify calls
    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        eq(CategoryDTO.CategoryName.ACTION_CREATED));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID_2);
    verify(partySvcClientService, times(1)).getParty(SAMPLE_UNIT_TYPE_HI, PARTY_ID);
    verify(partySvcClientService, times(1)).getParty(SAMPLE_UNIT_TYPE_H,
        PARTY_ID_PARENT_FOR_CASE_ID_2);
    verify(collectionExerciseClientService, times(1)).
        getCollectionExercise(COLLECTION_EXERCISE_ID);
    verify(surveySvcClientService, times(1)).requestDetailsForSurvey(CENSUS);
    // TODO Be more specific on the Action below once CTPA-1390 has been discussed & implemented
    verify(actionInstructionPublisher, times(1)).sendActionInstruction(eq(ACTIONEXPORTER),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Scenario where actionSvcStateTransitionManager throws an exception on transition
   */
  @Test
  public void testProcessActionCancelStateTransitionException() throws CTPException {
    when(actionSvcStateTransitionManager.transition(any(ActionDTO.ActionState.class),
        eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED))).thenThrow(new CTPException(CTPException.Fault.SYSTEM_ERROR,
        ACTION_STATE_TRANSITION_ERROR_MSG));

    try {
      Action action = new Action();
      actionProcessingService.processActionCancel(action);
      fail();
    } catch (CTPException e) {
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ACTION_STATE_TRANSITION_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never()).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Scenario where the action's state transitions OK but issue while persisting action to DB
   */
  @Test
  public void testProcessActionCancelPersistingException() throws CTPException {
    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new RuntimeException(DB_ERROR_MSG));

    try {
      Action action = new Action();
      actionProcessingService.processActionCancel(action);
      fail();
    } catch (RuntimeException e) {
      assertEquals(DB_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never()).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * An exception is thrown when creating a CaseEvent after the Action's state was transitioned and persisted OK.
   */
  @Test
  public void testProcessActionCancelCaseEventCreationThrowsException() throws CTPException {
    when(caseSvcClientService.createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class))).
        thenThrow(new RuntimeException(REST_ERROR_MSG));

    try {
      Action action = new Action();
      action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
      action.setId(ACTION_ID);
      actionProcessingService.processActionCancel(action);
      fail();
    } catch (RuntimeException e) {
      assertEquals(REST_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
    verify(actionInstructionPublisher, times(1)).sendActionInstruction(eq(ACTIONEXPORTER),
        actionCaptor.capture());
    uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel = (ActionCancel)actionCaptor.
        getValue();
    assertEquals(ACTION_ID.toString(), publishedActionCancel.getActionId());
    assertTrue(publishedActionCancel.isResponseRequired());
    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        eq(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED));
  }

  @Test
  public void testProcessActionCancelHappyPath() throws CTPException {
    Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setId(ACTION_ID);
    actionProcessingService.processActionCancel(action);

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        eq(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED));

    ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
    verify(actionInstructionPublisher, times(1)).sendActionInstruction(eq(ACTIONEXPORTER),
        actionCaptor.capture());
    uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel = (ActionCancel)actionCaptor.
        getValue();
    assertEquals(ACTION_ID.toString(), publishedActionCancel.getActionId());
    assertTrue(publishedActionCancel.isResponseRequired());
    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
  }

  @Test
  public void testActionInstructionNotSentIfInvalid() throws CTPException{
    // Start of section to mock responses
    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID)).thenReturn(caseDetailsDTOs.get(0));
    when(partySvcClientService.getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID)).thenReturn(partyDTOs.get(0));
    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID)). thenReturn(collectionExerciseDTOs.get(0));
    when(surveySvcClientService.requestDetailsForSurvey(CENSUS)).thenReturn(surveyDTOs.get(0));

    // End of section to mock responses

    // Start of section to run the test
    Action action = Action.builder()
            .id(ACTION_ID)
            .actionType(ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build())
            .caseId(CASE_ID)
            .priority(1).build();
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // VALIDATOR HAS STOPPED MESSAGE FROM BEING SENT
    verify(actionInstructionPublisher, times(0)).sendActionInstruction(eq(ACTIONEXPORTER),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  @Test
  public void testParseRespondentStatusesActive() {
    PartyDTO respondentActiveBI = partyDTOs.get(ACTIVE_BI);
    PartyDTO respondentCreatedBI = partyDTOs.get(CREATED_BI);

    when(partySvcClientService.getParty("BI", partyDTOs.get(B_PARTY).getAssociations().get(0).getPartyId())).thenReturn(respondentActiveBI);
    when(partySvcClientService.getParty("BI", partyDTOs.get(B_PARTY).getAssociations().get(1).getPartyId())).thenReturn(respondentCreatedBI);
    String respondentStatus = actionProcessingService.parseRespondentStatuses(partyDTOs.get(B_PARTY), "B");

    assertEquals(actionProcessingService.ACTIVE, respondentStatus);
  }

  @Test
  public void testParseRespondentStatusesCreated() {
    PartyDTO respondentSuspendedBI = partyDTOs.get(SUSPENDED_BI);
    PartyDTO respondentCreatedBI = partyDTOs.get(CREATED_BI);

    when(partySvcClientService.getParty("BI", partyDTOs.get(B_PARTY).getAssociations().get(0).getPartyId())).thenReturn(respondentSuspendedBI);
    when(partySvcClientService.getParty("BI", partyDTOs.get(B_PARTY).getAssociations().get(1).getPartyId())).thenReturn(respondentCreatedBI);

    String respondentStatus = actionProcessingService.parseRespondentStatuses(partyDTOs.get(B_PARTY), "B");

    assertEquals(actionProcessingService.CREATED, respondentStatus);
  }

  @Test
  public void testParseRespondentStatusesEmpty() {
    String respondentStatus = actionProcessingService.parseRespondentStatuses(partyDTOs.get(NO_ASSOCIATIONS_BI), "B");

    assertEquals(null, respondentStatus);
  }

  @Test
  public void testGetEnrolmentStatusEnabled () {
    PartyDTO partyDTO = partyDTOs.get(0);
    assertEquals(actionProcessingService.ENABLED, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusPending() {
    PartyDTO partyDTO = partyDTOs.get(1);
    assertEquals(actionProcessingService.PENDING, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusDefault() {
    PartyDTO partyDTO = partyDTOs.get(2);
    assertEquals(null, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusNoEnrolments() {
    PartyDTO partyDTO = partyDTOs.get(2);
    partyDTO.setAssociations(null);
    assertEquals(null, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGenerateTradingStyle() {
    Attributes businessAttributes = new Attributes();
    businessAttributes.setTradstyle1("TRADSTYLE1");
    businessAttributes.setTradstyle2("TRADSTYLE2");
    businessAttributes.setTradstyle3("TRADSTYLE3");

    String generatedTradingStyle = actionProcessingService.generateTradingStyle(businessAttributes);
    String expectedTradingStyle = "TRADSTYLE1 TRADSTYLE2 TRADSTYLE3";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testGenerateTradingStyleWithEmptyValues() {
    Attributes businessAttributes = new Attributes();

    String generatedTradingStyle = actionProcessingService.generateTradingStyle(businessAttributes);
    String expectedTradingStyle = "";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testGenerateTradingStyleWithSubsetOfTradingStyles() {
    Attributes businessAttributes = new Attributes();
    businessAttributes.setTradstyle1("TRADSTYLE1");
    businessAttributes.setTradstyle3("TRADSTYLE3");

    String generatedTradingStyle = actionProcessingService.generateTradingStyle(businessAttributes);
    String expectedTradingStyle = "TRADSTYLE1 TRADSTYLE3";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }
}
