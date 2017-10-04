package uk.gov.ons.ctp.response.action.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.CaseSvc;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

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

  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action State transition failed.";
  private static final String DB_ERROR_MSG = "DB is KO.";
  private static final String REST_ERROR_MSG = "REST call is KO.";

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
//    partys = FixtureHelper.loadClassFixtures(PartyDTO[].class);
//    caseDetailsDTOs = FixtureHelper.loadClassFixtures(CaseDetailsDTO[].class);
//    collectionExerciseDTOs = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);

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
}
