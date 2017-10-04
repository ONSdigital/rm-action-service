package uk.gov.ons.ctp.response.action.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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

  private static final UUID ACTION_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d1");
  private static final UUID CASE_ID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");
  private static final UUID COLLECTION_EXERCISE_ID = UUID.fromString("c2124abc-10c6-4c7c-885a-779d185a03a4");
  private static final UUID PARTY_ID = UUID.fromString("2e6add83-e43d-4f52-954f-4109be506c86");

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

//    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
//    householdInitialContactActions = FixtureHelper.loadClassFixtures(Action[].class, HOUSEHOLD_INITIAL_CONTACT);
//    householdUploadIACActions = FixtureHelper.loadClassFixtures(Action[].class, HOUSEHOLD_UPLOAD_IAC);
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
        any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
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
        any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
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
    when(caseSvcClientService.createNewCaseEvent(any(Action.class), any(CategoryDTO.CategoryName.class))).
        thenThrow(new RuntimeException(REST_ERROR_MSG));

    Action action = new Action();
    action.setActionType(ActionType.builder().responseRequired(Boolean.TRUE).build());
    try {
      actionProcessingService.processActionRequest(action);
      fail();
    } catch (RuntimeException e) {
      assertEquals(REST_ERROR_MSG, e.getMessage());
    }

    verify(actionSvcStateTransitionManager, times(1)).transition(
        any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionPlanRepo, never()).findOne(any(Integer.class));
    verify(caseSvcClientService, never()).getCaseWithIACandCaseEvents(any(UUID.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }

  /**
   * Happy path for an action linked to a case for a PARENT sample unit (a H one), ie we go all the way to producing
   * an ActionRequest and publishing it.
   */
  @Test
  public void testProcessActionRequestHappyPath() throws CTPException {
    // Start of section to mock responses
    ActionPlan actionPlan = ActionPlan.builder().name(ACTION_PLAN_NAME).build();
    when(actionPlanRepo.findOne(ACTION_PLAN_FK)).thenReturn(actionPlan);

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID)).thenReturn(caseDetailsDTOs.get(0));

    when(partySvcClientService.getParty(SAMPLE_UNIT_TYPE_H, PARTY_ID)).thenReturn(partyDTOs.get(0));

    when(collectionExerciseClientService.getCollectionExercise(COLLECTION_EXERCISE_ID)).
        thenReturn(collectionExerciseDTOs.get(0));

    when(surveySvcClientService.requestDetailsForSurvey(CENSUS)).thenReturn(surveyDTOs.get(0));
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
        any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(any(UUID.class));
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

    when(caseSvcClientService.getCaseWithIACandCaseEvents(CASE_ID)).thenReturn(caseDetailsDTOs.get(1)); // the returned case has a sample unit type of Z
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
        any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class));
    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
    verify(caseSvcClientService, times(1)).createNewCaseEvent(any(Action.class),
        any(CategoryDTO.CategoryName.class));
    verify(actionPlanRepo, times(1)).findOne(ACTION_PLAN_FK);
    verify(caseSvcClientService, times(1)).getCaseWithIACandCaseEvents(any(UUID.class));
    verify(partySvcClientService, never()).getParty(any(String.class), any(UUID.class));
    verify(collectionExerciseClientService, never()).getCollectionExercise(any(UUID.class));
    verify(surveySvcClientService, never()).requestDetailsForSurvey(any(String.class));
    verify(actionInstructionPublisher, never()).sendActionInstruction(any(String.class),
        any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }
}
