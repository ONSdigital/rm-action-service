package uk.gov.ons.ctp.response.action.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.action.service.impl.ActionProcessingServiceImpl.CANCELLATION_REASON;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.Attributes;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

/** Tests for the ActionProcessingServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionProcessingServiceImplTest {

  private static final Integer ACTION_PLAN_FK = 1;

  private static final String ACTIONEXPORTER = "actionExporter";
  private static final String ACTION_PLAN_NAME = "action plan 1";
  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action State transition failed.";
  private static final String CENSUS_SURVEY_ID = "Census2021";
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
  private static final UUID COLLECTION_EXERCISE_ID =
      UUID.fromString("c2124abc-10c6-4c7c-885a-779d185a03a4");
  private static final UUID PARTY_ID = UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c86");
  private static final UUID PARTY_ID_PARENT_FOR_CASE_ID_2 =
      UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c81");

  @Spy private AppConfig appConfig = new AppConfig();

  @Mock private CaseSvcClientService caseSvcClientService;

  @Mock private CollectionExerciseClientService collectionExerciseClientService;

  @Mock private PartySvcClientService partySvcClientService;

  @Mock private SurveySvcClientService surveySvcClientService;

  @Mock private ActionRepository actionRepo;

  @Mock private ActionPlanRepository actionPlanRepo;

  @Mock private ActionInstructionPublisher actionInstructionPublisher;

  @Mock
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  @Mock private ActionRequestValidator validator;

  @InjectMocks private ActionProcessingServiceImpl actionProcessingService;

  private List<CaseDetailsDTO> caseDetailsDTOs;
  private List<CollectionExerciseDTO> collectionExerciseDTOs;
  private List<PartyDTO> partyDTOs;
  private List<SurveyDTO> surveyDTOs;

  /** Initialises Mockito and loads Class Fixtures */
  @Before
  public void setUp() throws Exception {
    final CaseSvc caseSvcConfig = new CaseSvc();
    appConfig.setCaseSvc(caseSvcConfig);

    partyDTOs = FixtureHelper.loadClassFixtures(PartyDTO[].class);
    caseDetailsDTOs = FixtureHelper.loadClassFixtures(CaseDetailsDTO[].class);
    collectionExerciseDTOs = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    surveyDTOs = FixtureHelper.loadClassFixtures(SurveyDTO[].class);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testProcessActionRequestNoActionType() throws CTPException {
    final Action action = new Action();
    actionProcessingService.processActionRequest(action);

    verify(actionSvcStateTransitionManager, never())
        .transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never())
        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  @Test
  public void testProcessActionRequestActionTypeWithNoResponseRequired() throws CTPException {
    final Action action = new Action();
    action.setActionType(ActionType.builder().build());
    actionProcessingService.processActionRequest(action);

    verify(actionSvcStateTransitionManager, never())
        .transition(any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never())
        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** An exception is thrown when transitioning the state of the Action */
  @Test
  public void testProcessActionRequestActionStateTransitionThrowsException() throws CTPException {
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenThrow(
            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));

    final Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
    try {
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (final CTPException e) {
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ACTION_STATE_TRANSITION_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never())
        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** An exception is thrown when saving the Action after its state was transitioned */
  @Test
  public void testProcessActionRequestActionPersistingActionThrowsException() throws CTPException {
    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new RuntimeException(DB_ERROR_MSG));

    final Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
    try {
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (final RuntimeException e) {
      assertEquals(DB_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never())
        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * An exception is thrown when creating a CaseEvent after the Action's state was transitioned and
   * persisted OK.
   */
  @Test
  public void testProcessActionRequestCaseEventCreationThrowsException() throws CTPException {
    // Start of section to mock responses
    final ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID))
        .thenReturn(caseDetailsDTOs.get(0));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SAMPLE_UNIT_TYPE_H, PARTY_ID, CENSUS_SURVEY_ID))
        .thenReturn(partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID))
        .thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS_SURVEY_ID))
        .thenReturn(surveyDTOs.get(0));

    when(caseSvcClientService.createNewCaseEvent(
            any(Action.class), any(CategoryDTO.CategoryName.class)))
        .thenThrow(new RuntimeException(REST_ERROR_MSG));

    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenReturn(ActionDTO.ActionState.PENDING);
    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    // End of section to mock responses

    try {
      final Action action = new Action();
      action.setId(ACTION_ID);
      action.setActionType(
          ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
      action.setActionPlanFK(ACTION_PLAN_FK);
      action.setCaseId(CASE_ID);
      action.setPriority(1);
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (final RuntimeException e) {
      assertEquals(REST_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID);
    verify(partySvcClientService, times(1))
        .getPartyWithAssociationsFilteredBySurvey(SAMPLE_UNIT_TYPE_H, PARTY_ID, CENSUS_SURVEY_ID);
    verify(partySvcClientService, never()).getParty(eq(SAMPLE_UNIT_TYPE_HI), any(UUID.class));
    verify(collectionExerciseClientService, times(1)).getCollectionExercise(COLLECTION_EXERCISE_ID);
    verify(surveySvcClientService, times(1)).requestDetailsForSurvey(CENSUS_SURVEY_ID);
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
    verify(caseSvcClientService, times(1))
        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
  }

  /**
   * Happy path for an action linked to a case for a PARENT sample unit (a H one), ie we go all the
   * way to producing an ActionRequest and publishing it.
   */
  @Test
  public void testProcessActionRequestHappyPathParentUnit() throws CTPException {
    // Start of section to mock responses
    final ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID))
        .thenReturn(caseDetailsDTOs.get(0));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SAMPLE_UNIT_TYPE_H, PARTY_ID, CENSUS_SURVEY_ID))
        .thenReturn(partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID))
        .thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS_SURVEY_ID))
        .thenReturn(surveyDTOs.get(0));

    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenReturn(ActionDTO.ActionState.PENDING);
    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    // End of section to mock responses

    // Start of section to run the test
    final Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setActionPlanFK(ACTION_PLAN_FK);
    action.setCaseId(CASE_ID);
    action.setPriority(1);
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // Start of section to verify calls
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1))
        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID);
    verify(partySvcClientService, times(1))
        .getPartyWithAssociationsFilteredBySurvey(SAMPLE_UNIT_TYPE_H, PARTY_ID, CENSUS_SURVEY_ID);
    verify(collectionExerciseClientService, times(1)).getCollectionExercise(COLLECTION_EXERCISE_ID);
    verify(surveySvcClientService, times(1)).requestDetailsForSurvey(CENSUS_SURVEY_ID);
    // TODO Be more specific on the Action below once CTPA-1390 has been discussed & implemented
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(
            eq(ACTIONEXPORTER),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Test where an action is linked to a case for a sample unit which is not recognised (ie none of
   * the expected values H, HI, CI, B, BI)
   */
  @Test
  public void testProcessActionRequestForActionLinkedToInvalidSampleUnitType() throws CTPException {
    // Start of section to mock responses
    final ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    // the returned case has a sample unit type of Z
    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID_1))
        .thenReturn(caseDetailsDTOs.get(1));
    // End of section to mock responses

    // Start of section to run the test
    final Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setActionPlanFK(ACTION_PLAN_FK);
    action.setCaseId(CASE_ID_1);
    action.setPriority(1);
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // Start of section to verify calls
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1))
        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID_1);
    verify(partySvcClientService, never()).getParty(any(String.class), any(UUID.class));
    verify(collectionExerciseClientService, never()).getCollectionExercise(any(UUID.class));
    verify(surveySvcClientService, never()).requestDetailsForSurvey(any(String.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Happy path for an action linked to a case for a CHILD sample unit (a HI one), ie we go all the
   * way to producing an ActionRequest and publishing it.
   */
  @Test
  public void testProcessActionRequestHappyPathChildUnit() throws CTPException {
    // Start of section to mock responses
    final ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID_2))
        .thenReturn(caseDetailsDTOs.get(2));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SAMPLE_UNIT_TYPE_HI, PARTY_ID, CENSUS_SURVEY_ID))
        .thenReturn(partyDTOs.get(1));
    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SAMPLE_UNIT_TYPE_H, PARTY_ID_PARENT_FOR_CASE_ID_2, CENSUS_SURVEY_ID))
        .thenReturn(partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID))
        .thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS_SURVEY_ID))
        .thenReturn(surveyDTOs.get(0));
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenReturn(ActionDTO.ActionState.PENDING);
    when(validator.validate(any(ActionType.class), any(ActionRequest.class))).thenReturn(true);
    // End of section to mock responses

    // Start of section to run the test
    final Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setActionPlanFK(ACTION_PLAN_FK);
    action.setCaseId(CASE_ID_2);
    action.setPriority(1);
    actionProcessingService.processActionRequest(action);
    // End of section to run the test

    // Start of section to verify calls
    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.REQUEST_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1))
        .createNewCaseEvent(any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CREATED));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(CASE_ID_2);
    verify(partySvcClientService, times(1))
        .getPartyWithAssociationsFilteredBySurvey(
            SAMPLE_UNIT_TYPE_H, PARTY_ID_PARENT_FOR_CASE_ID_2, CENSUS_SURVEY_ID);
    verify(collectionExerciseClientService, times(1)).getCollectionExercise(COLLECTION_EXERCISE_ID);
    verify(surveySvcClientService, times(1)).requestDetailsForSurvey(CENSUS_SURVEY_ID);
    // TODO Be more specific on the Action below once CTPA-1390 has been discussed & implemented
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(
            eq(ACTIONEXPORTER),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** Scenario where actionSvcStateTransitionManager throws an exception on transition */
  @Test
  public void testProcessActionCancelStateTransitionException() throws CTPException {
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED)))
        .thenThrow(
            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));

    try {
      final Action action = new Action();
      actionProcessingService.processActionCancel(action);
      fail();
    } catch (final CTPException e) {
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ACTION_STATE_TRANSITION_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, never()).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never())
        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /** Scenario where the action's state transitions OK but issue while persisting action to DB */
  @Test
  public void testProcessActionCancelPersistingException() throws CTPException {
    when(actionRepo.saveAndFlush(any(Action.class))).thenThrow(new RuntimeException(DB_ERROR_MSG));

    try {
      final Action action = new Action();
      actionProcessingService.processActionCancel(action);
      fail();
    } catch (final RuntimeException e) {
      assertEquals(DB_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, never())
        .createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class));
    verify(actionInstructionPublisher, never())
        .sendActionInstruction(
            any(String.class),
            any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * An exception is thrown when creating a CaseEvent after the Action's state was transitioned and
   * persisted OK.
   */
  @Test
  public void testProcessActionCancelCaseEventCreationThrowsException() throws CTPException {
    when(caseSvcClientService.createNewCaseEvent(
            any(Action.class), any(CategoryDTO.CategoryName.class)))
        .thenThrow(new RuntimeException(REST_ERROR_MSG));

    try {
      final Action action = new Action();
      action.setActionType(
          ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
      action.setId(ACTION_ID);
      actionProcessingService.processActionCancel(action);
      fail();
    } catch (final RuntimeException e) {
      assertEquals(REST_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    final ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(eq(ACTIONEXPORTER), actionCaptor.capture());
    final uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel =
        (ActionCancel) actionCaptor.getValue();
    assertEquals(ACTION_ID.toString(), publishedActionCancel.getActionId());
    assertTrue(publishedActionCancel.isResponseRequired());
    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
    verify(caseSvcClientService, times(1))
        .createNewCaseEvent(
            any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED));
  }

  @Test
  public void testProcessActionCancelHappyPath() throws CTPException {
    final Action action = new Action();
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setId(ACTION_ID);
    actionProcessingService.processActionCancel(action);

    verify(actionSvcStateTransitionManager, times(1))
        .transition(
            any(ActionDTO.ActionState.class), eq(ActionDTO.ActionEvent.CANCELLATION_DISTRIBUTED));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1))
        .createNewCaseEvent(
            any(Action.class), eq(CategoryDTO.CategoryName.ACTION_CANCELLATION_CREATED));

    final ArgumentCaptor<uk.gov.ons.ctp.response.action.message.instruction.Action> actionCaptor =
        ArgumentCaptor.forClass(uk.gov.ons.ctp.response.action.message.instruction.Action.class);
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(eq(ACTIONEXPORTER), actionCaptor.capture());
    final uk.gov.ons.ctp.response.action.message.instruction.ActionCancel publishedActionCancel =
        (ActionCancel) actionCaptor.getValue();
    assertEquals(ACTION_ID.toString(), publishedActionCancel.getActionId());
    assertTrue(publishedActionCancel.isResponseRequired());
    assertEquals(CANCELLATION_REASON, publishedActionCancel.getReason());
  }

  @Test
  public void testGenerateChildPartyMap() {
    final PartyDTO respondentSuspendedBI = partyDTOs.get(SUSPENDED_BI);
    final PartyDTO respondentCreatedBI = partyDTOs.get(CREATED_BI);

    when(partySvcClientService.getParty(
            "BI", partyDTOs.get(B_PARTY).getAssociations().get(0).getPartyId()))
        .thenReturn(respondentSuspendedBI);
    when(partySvcClientService.getParty(
            "BI", partyDTOs.get(B_PARTY).getAssociations().get(1).getPartyId()))
        .thenReturn(respondentCreatedBI);

    final Map<String, PartyDTO> expectedChildPartyMap = new HashMap<>();
    expectedChildPartyMap.put("SUSPENDED", respondentSuspendedBI);
    expectedChildPartyMap.put(actionProcessingService.CREATED, respondentCreatedBI);

    final Map<String, PartyDTO> actualChildPartyMap =
        actionProcessingService.getChildParties(partyDTOs.get(B_PARTY), "B");

    assertEquals(expectedChildPartyMap, actualChildPartyMap);
  }

  @Test
  public void testParseRespondentStatusCreated() {
    final Map<String, PartyDTO> childPartyMap = new HashMap<>();
    childPartyMap.put(actionProcessingService.CREATED, partyDTOs.get(CREATED_BI));
    childPartyMap.put("SUSPENDED", partyDTOs.get(SUSPENDED_BI));

    final String respondentStatus = actionProcessingService.parseRespondentStatuses(childPartyMap);

    assertEquals(actionProcessingService.CREATED, respondentStatus);
  }

  @Test
  public void testParseRespondentStatusActive() {
    final Map<String, PartyDTO> childPartyMap = new HashMap<>();
    childPartyMap.put(actionProcessingService.CREATED, partyDTOs.get(CREATED_BI));
    childPartyMap.put(actionProcessingService.ACTIVE, partyDTOs.get(ACTIVE_BI));

    final String respondentStatus = actionProcessingService.parseRespondentStatuses(childPartyMap);

    assertEquals(actionProcessingService.ACTIVE, respondentStatus);
  }

  @Test
  public void testParseRespondentStatusesEmpty() {
    final Map<String, PartyDTO> childPartyMap = new HashMap<>();
    final String respondentStatus = actionProcessingService.parseRespondentStatuses(childPartyMap);

    assertEquals(null, respondentStatus);
  }

  @Test
  public void testGetEnrolmentStatusEnabled() {
    final PartyDTO partyDTO = partyDTOs.get(0);
    assertEquals(
        actionProcessingService.ENABLED, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusPending() {
    final PartyDTO partyDTO = partyDTOs.get(1);
    assertEquals(
        actionProcessingService.PENDING, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusDefault() {
    final PartyDTO partyDTO = partyDTOs.get(2);
    assertEquals(null, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusNoEnrolments() {
    final PartyDTO partyDTO = partyDTOs.get(2);
    partyDTO.setAssociations(null);
    assertEquals(null, actionProcessingService.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGenerateTradingStyle() {
    final Attributes businessAttributes = new Attributes();
    businessAttributes.setTradstyle1("TRADSTYLE1");
    businessAttributes.setTradstyle2("TRADSTYLE2");
    businessAttributes.setTradstyle3("TRADSTYLE3");

    final String generatedTradingStyle =
        actionProcessingService.generateTradingStyle(businessAttributes);
    final String expectedTradingStyle = "TRADSTYLE1 TRADSTYLE2 TRADSTYLE3";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testGenerateTradingStyleWithEmptyValues() {
    final Attributes businessAttributes = new Attributes();

    final String generatedTradingStyle =
        actionProcessingService.generateTradingStyle(businessAttributes);
    final String expectedTradingStyle = "";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testGenerateTradingStyleWithSubsetOfTradingStyles() {
    final Attributes businessAttributes = new Attributes();
    businessAttributes.setTradstyle1("TRADSTYLE1");
    businessAttributes.setTradstyle3("TRADSTYLE3");

    final String generatedTradingStyle =
        actionProcessingService.generateTradingStyle(businessAttributes);
    final String expectedTradingStyle = "TRADSTYLE1 TRADSTYLE3";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testCaseRefShouldBeOnActionRequest() throws CTPException {
    // Given
    CaseDetailsDTO caseDetails = createCaseDetails();
    caseDetails.setCaseRef("Case ref");
    given(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID)).willReturn(caseDetails);
    given(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID))
        .willReturn(createCollectionExercise());
    given(surveySvcClientService.requestDetailsForSurvey(CENSUS_SURVEY_ID))
        .willReturn(new SurveyDTO());
    given(
            partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
                SAMPLE_UNIT_TYPE_H, PARTY_ID, CENSUS_SURVEY_ID))
        .willReturn(createParty());
    final Action action = new Action();
    action.setId(ACTION_ID);
    action.setActionType(
        ActionType.builder().responseRequired(Boolean.TRUE).handler(ACTIONEXPORTER).build());
    action.setCaseId(CASE_ID);
    action.setPriority(1);

    // When
    actionProcessingService.processActionRequest(action);

    // Then
    ArgumentCaptor<ActionRequest> captor = ArgumentCaptor.forClass(ActionRequest.class);
    verify(actionInstructionPublisher).sendActionInstruction(eq(ACTIONEXPORTER), captor.capture());
    assertEquals("Case ref", captor.getValue().getCaseRef());
  }

  private CollectionExerciseDTO createCollectionExercise() {
    CollectionExerciseDTO collectionExercise = new CollectionExerciseDTO();
    collectionExercise.setSurveyId(CENSUS_SURVEY_ID);
    return collectionExercise;
  }

  private CaseDetailsDTO createCaseDetails() {
    CaseDetailsDTO caseDetails = new CaseDetailsDTO();
    caseDetails.setCaseEvents(Collections.emptyList());
    caseDetails.setSampleUnitType(SampleUnitDTO.SampleUnitType.H.toString());
    caseDetails.setCaseGroup(createCaseGroup());
    caseDetails.setPartyId(PARTY_ID);
    return caseDetails;
  }

  private CaseGroupDTO createCaseGroup() {
    CaseGroupDTO caseGroup = new CaseGroupDTO();
    caseGroup.setCaseGroupStatus(CaseGroupStatus.INPROGRESS);
    caseGroup.setCollectionExerciseId(COLLECTION_EXERCISE_ID);
    return caseGroup;
  }

  private PartyDTO createParty() {
    PartyDTO party = new PartyDTO();
    party.setAttributes(new Attributes());
    party.setAssociations(Collections.emptyList());
    return party;
  }
}
