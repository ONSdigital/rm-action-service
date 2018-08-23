package uk.gov.ons.ctp.response.action.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.model.PotentialAction;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

/** Tests for ActionServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionServiceTest {

  private static final UUID ACTION_CASEID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");
  private static final UUID ACTION_ID_0 = UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72b3e");
  private static final UUID ACTION_ID_3 = UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72bd9");
  private static final String ACTION_TYPENAME = "HouseholdInitialContact";

  @InjectMocks private ActionService actionService;

  @Mock private ActionCaseRepository actionCaseRepository;

  @Mock private ActionPlanJobRepository actionPlanJobRepository;

  @Mock private ActionPlanRepository actionPlanRepository;

  @Mock private ActionRepository actionRepo;

  @Mock private ActionTypeRepository actionTypeRepo;

  @Mock private StateTransitionManager<ActionState, ActionEvent> actionSvcStateTransitionManager;

  private List<Action> actions;
  private List<ActionFeedback> actionFeedback;
  private List<ActionType> actionType;

  /**
   * Initialises Mockito and loads Class Fixtures
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    actions = FixtureHelper.loadClassFixtures(Action[].class);
    actionFeedback = FixtureHelper.loadClassFixtures(ActionFeedback[].class);
    actionType = FixtureHelper.loadClassFixtures(ActionType[].class);
  }

  @Test
  public void cancelActionsForACaseAndVerifyActionsAreUpdatedAndFlushedActionsReturned()
      throws Exception {
    when(actionRepo.findByCaseId(ACTION_CASEID)).thenReturn(actions);
    when(actionSvcStateTransitionManager.transition(
            ActionState.PENDING, ActionEvent.REQUEST_CANCELLED))
        .thenReturn(ActionState.CANCELLED);
    when(actionSvcStateTransitionManager.transition(
            ActionState.SUBMITTED, ActionEvent.REQUEST_CANCELLED))
        .thenReturn(ActionState.CANCELLED);
    when(actionSvcStateTransitionManager.transition(
            ActionState.ACTIVE, ActionEvent.REQUEST_CANCELLED))
        .thenReturn(ActionState.CANCELLED);

    final List<Action> flushedActions = actionService.cancelActions(ACTION_CASEID);

    for (final Action action : actions) {
      if (action.getActionType().getCanCancel()) {
        assertThat(action.getState()).isEqualTo(ActionState.CANCELLED);
      } else {
        assertThat(action.getState()).isNotEqualTo(ActionState.CANCELLED);
      }
    }
    final List<Action> originalActions = FixtureHelper.loadClassFixtures(Action[].class);

    verify(actionRepo, times(1)).findByCaseId(ACTION_CASEID);
    verify(actionSvcStateTransitionManager, times(1))
        .transition(originalActions.get(0).getState(), ActionEvent.REQUEST_CANCELLED);
    verify(actionSvcStateTransitionManager, times(1))
        .transition(originalActions.get(1).getState(), ActionEvent.REQUEST_CANCELLED);
    verify(actionRepo, times(1)).saveAndFlush(actions.get(0));
    verify(actionRepo, times(1)).saveAndFlush(actions.get(1));

    assertThat(flushedActions).contains(actions.get(0), actions.get(1));
  }

  @Test
  public void feedbackActionVerifyActionAreUpdatedAndStateHasChanged() throws Exception {
    when(actionRepo.findById(ACTION_ID_0)).thenReturn(actions.get(0));
    when(actionSvcStateTransitionManager.transition(
            ActionState.PENDING, ActionEvent.REQUEST_COMPLETED))
        .thenReturn(ActionState.COMPLETED);
    when(actionRepo.saveAndFlush(any())).then(returnsFirstArg());

    actionService.feedBackAction(actionFeedback.get(0));

    final ActionEvent event = ActionEvent.valueOf(actionFeedback.get(0).getOutcome().name());
    final Action originalAction = FixtureHelper.loadClassFixtures(Action[].class).get(0);

    verify(actionRepo, times(1)).findById(ACTION_ID_0);
    verify(actionRepo, times(1)).saveAndFlush(actions.get(0));
    verify(actionSvcStateTransitionManager, times(1)).transition(originalAction.getState(), event);
  }

  @Test
  public void whenFeedbackActionNotFoundVerifySaveIsntCalled() throws Exception {
    actionService.feedBackAction(actionFeedback.get(0));

    verify(actionRepo, times(1)).findById(any());
    verify(actionRepo, times(0)).saveAndFlush(any());
    verify(actionSvcStateTransitionManager, times(0)).transition(any(), any());
  }

  @Test
  public void whenFeedbackActionWithNullActionIdVerifySaveIsntCalled() throws Exception {
    final ActionFeedback actionFeedbackWithNullActionId = new ActionFeedback();
    actionService.feedBackAction(actionFeedbackWithNullActionId);

    verify(actionRepo, times(0)).findById(any());
    verify(actionRepo, times(0)).saveAndFlush(any());
    verify(actionSvcStateTransitionManager, times(0)).transition(any(), any());
  }

  @Test
  public void checkCreateActionIsSaved() {
    when(actionTypeRepo.findByName(ACTION_TYPENAME)).thenReturn(actionType.get(0));
    actionService.createAction(actions.get(0));
    verify(actionRepo, times(1)).saveAndFlush(actions.get(0));
  }

  @Test
  public void testUpdateActionCallsSaveEvent() {
    final Action action = actions.get(0);
    when(actionRepo.findById(ACTION_ID_0)).thenReturn(action);
    when(actionRepo.saveAndFlush(any())).then(returnsFirstArg());

    actionService.updateAction(action);

    verify(actionRepo, times(1)).saveAndFlush(any());
  }

  @Test
  public void testUpdateActionNoActionFound() {
    final Action existingAction = actionService.updateAction(actions.get(0));

    verify(actionRepo, times(0)).saveAndFlush(any());
    assertThat(existingAction).isNull();
  }

  @Test
  public void testUpdateActionNoUpdate() {
    when(actionRepo.findById(ACTION_ID_3)).thenReturn(actions.get(3));
    final Action existingAction = actionService.updateAction(actions.get(3));

    verify(actionRepo, times(0)).saveAndFlush(any());
    assertThat(existingAction).isEqualTo(actions.get(3));
  }

  @Test
  public void testCreateScheduledActionsWithoutAnActionPlanJobNothingHappens() {
    actionService.createScheduledActions(1);

    assertEquals(0, actionRepo.count());
  }

  @Test
  public void testCreateScheduledActionsWithNoActiveCaseDoesNotCreateActions() {
    //// Given
    // Create Action Plan
    final int actionPlanPK = 1;
    final ActionPlan actionPlan = mock(ActionPlan.class);

    // Create Action Plan Job for Action Plan
    final int actionPlanJobPK = 1;
    final ActionPlanJob actionPlanJob = mock(ActionPlanJob.class);
    when(actionPlanJob.getActionPlanFK()).thenReturn(actionPlanPK);

    when(actionPlanJobRepository.findByActionPlanJobPK(actionPlanJobPK)).thenReturn(actionPlanJob);
    when(actionPlanRepository.findByActionPlanPK(actionPlanPK)).thenReturn(actionPlan);

    when(actionRepo.findPotentialActionsActiveDate(eq(actionPlanPK), any(Timestamp.class)))
        .thenReturn(new ArrayList<>());

    //// When
    actionService.createScheduledActions(actionPlanJobPK);

    //// Then
    // action plan has been updated
    verify(actionPlan, times(1)).setLastRunDateTime(any(Timestamp.class));

    // action plan job has been updated
    verify(actionPlanJob, times(1)).complete(any(Timestamp.class));

    // did not attempt to create actions
    verify(actionRepo, times(0)).save(any(Action.class));
    verify(actionRepo, times(1)).flush();
  }

  @Test
  public void testCreateScheduledActionsWithActiveCaseCreatesActions() {
    //// Given
    // Create Action Plan
    final int actionPlanPK = 1;
    final ActionPlan actionPlan = mock(ActionPlan.class);
    when(actionPlan.getActionPlanPK()).thenReturn(actionPlanPK);

    // Create Action Plan Job for Action Plan
    final int actionPlanJobPK = 1;
    final ActionPlanJob actionPlanJob = mock(ActionPlanJob.class);
    when(actionPlanJob.getActionPlanFK()).thenReturn(actionPlanPK);

    final List<PotentialAction> potentialActions = new ArrayList<>();
    final PotentialAction potentialAction = mock(PotentialAction.class);
    potentialActions.add(potentialAction);

    when(actionPlanJobRepository.findByActionPlanJobPK(actionPlanJobPK)).thenReturn(actionPlanJob);
    when(actionPlanRepository.findByActionPlanPK(actionPlanPK)).thenReturn(actionPlan);
    when(actionRepo.findPotentialActionsActiveDate(eq(actionPlanPK), any(Timestamp.class)))
        .thenReturn(potentialActions);

    //// When
    actionService.createScheduledActions(actionPlanJobPK);

    //// Then
    // action plan has been updated
    verify(actionPlan, times(1)).setLastRunDateTime(any(Timestamp.class));

    // action plan job has been updated
    verify(actionPlanJob, times(1)).complete(any(Timestamp.class));

    // attempted to create actions
    verify(actionRepo, times(1)).save(any(Action.class));
    verify(actionRepo, times(1)).flush();
  }

  @Test
  public void ensureReturnByDateFormattedForSocial() {
    SimpleDateFormat expectedDateFormat = new SimpleDateFormat("dd/MM");
    CollectionExerciseAndSurvey decorator = new CollectionExerciseAndSurvey();
    ActionRequest actionRequest = new ActionRequest();

    ActionRequestContext context = createActionRequestContext(SampleUnitDTO.SampleUnitType.H);

    decorator.decorateActionRequest(actionRequest, context);

    assertThat(actionRequest.getReturnByDate())
        .isEqualTo(
            expectedDateFormat.format(
                context.getCollectionExercise().getScheduledReturnDateTime()));
  }

  @Test
  public void ensureReturnByDateFormattedForBusiness() {
    SimpleDateFormat expectedDateFormat =
        new SimpleDateFormat(ActionProcessingService.DATE_FORMAT_IN_REMINDER_EMAIL);
    CollectionExerciseAndSurvey decorator = new CollectionExerciseAndSurvey();
    ActionRequest actionRequest = new ActionRequest();

    ActionRequestContext context = createActionRequestContext(SampleUnitDTO.SampleUnitType.B);

    decorator.decorateActionRequest(actionRequest, context);

    assertThat(actionRequest.getReturnByDate())
        .isEqualTo(
            expectedDateFormat.format(
                context.getCollectionExercise().getScheduledReturnDateTime()));
  }

  private ActionRequestContext createActionRequestContext(
      SampleUnitDTO.SampleUnitType sampleUnitType) {
    ActionRequestContext context = new ActionRequestContext();
    Date date = new Date();

    CollectionExerciseDTO collectionExercise = new CollectionExerciseDTO();
    collectionExercise.setExerciseRef("123");
    collectionExercise.setUserDescription("Test Description");
    collectionExercise.setScheduledReturnDateTime(new Timestamp(date.getTime()));
    context.setCollectionExercise(collectionExercise);

    SurveyDTO survey = new SurveyDTO();
    survey.setLongName("Test Survey Long Name");
    survey.setLegalBasis("Test Basis");
    survey.setSurveyRef("001");
    context.setSurvey(survey);
    context.setSampleUnitType(sampleUnitType);
    return context;
  }
}
