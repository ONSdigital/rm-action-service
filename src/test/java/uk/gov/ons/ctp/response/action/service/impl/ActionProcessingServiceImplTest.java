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
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests for the ActionProcessingServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionProcessingServiceImplTest {

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
}
