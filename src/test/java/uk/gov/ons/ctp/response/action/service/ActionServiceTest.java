package uk.gov.ons.ctp.response.action.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Collections;
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
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRuleRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

/** Tests for ActionService */
@RunWith(MockitoJUnitRunner.class)
public class ActionServiceTest {

  private static final UUID ACTION_CASEID = UUID.fromString("7fac359e-645b-487e-bb02-70536eae51d4");
  private static final UUID ACTION_ID_0 = UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72b3e");
  private static final UUID ACTION_ID_3 = UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72bd9");
  private static final String ACTION_TYPENAME = "HouseholdInitialContact";

  @InjectMocks private ActionService actionService;

  @Mock private StateTransitionManager<ActionState, ActionEvent> actionSvcStateTransitionManager;

  @Mock private ActionRepository actionRepo;
  @Mock private ActionCaseRepository actionCaseRepo;
  @Mock private ActionPlanRepository actionPlanRepo;
  @Mock private ActionPlanJobRepository actionPlanJobRepo;
  @Mock private ActionRuleRepository actionRuleRepo;
  @Mock private ActionTypeRepository actionTypeRepo;

  @Mock private CollectionExerciseClientService collectionExerciseClientService;
  @Mock private PartySvcClientService partySvcClientService;

  private List<Action> actions;
  private List<ActionCase> actionCases;
  private ActionCase actionBCase;
  private List<ActionFeedback> actionFeedback;
  private List<ActionRule> actionRules;
  private List<ActionType> actionTypes;
  private ActionPlanJob actionPlanJob;
  private List<CollectionExerciseDTO> collectionExercises;
  private List<PartyDTO> partys;

  /**
   * Initialises Mockito and loads Class Fixtures
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    actions = FixtureHelper.loadClassFixtures(Action[].class);
    actionCases = FixtureHelper.loadClassFixtures(ActionCase[].class);
    actionBCase = actionCases.get(0);
    actionFeedback = FixtureHelper.loadClassFixtures(ActionFeedback[].class);
    actionRules = FixtureHelper.loadClassFixtures(ActionRule[].class);
    actionRules.forEach(actionRule -> actionRule.setTriggerDateTime(OffsetDateTime.now()));
    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
    actionPlanJob = new ActionPlanJob();

    collectionExercises = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    partys = FixtureHelper.loadClassFixtures(PartyDTO[].class);
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
    when(actionTypeRepo.findByName(ACTION_TYPENAME)).thenReturn(actionTypes.get(0));
    actionService.createAdHocAction(actions.get(0));
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
  public void testCreateScheduledActions() {

    // Given
    when(actionCaseRepo.findByActionPlanFK(any()))
        .thenReturn(Collections.singletonList(actionCases.get(0)));
    when(actionRuleRepo.findByActionPlanFK(any()))
        .thenReturn(Collections.singletonList(actionRules.get(0)));
    when(actionTypeRepo.findByActionTypePK(any())).thenReturn(actionTypes.get(0));

    // When
    ActionPlan actionPlan = new ActionPlan();
    actionPlan.setActionPlanPK(1);
    actionService.createScheduledActions(actionPlan, actionPlanJob);

    // Then
    verify(actionRepo, times(1)).saveAndFlush(any());
    verify(actionPlanRepo, times(1)).saveAndFlush(any());
    verify(actionPlanJobRepo, times(1)).saveAndFlush(any());
  }

  @Test
  public void ensureReturnByDateFormattedForSocial() {
    SimpleDateFormat expectedDateFormat = new SimpleDateFormat("dd/MM");
    CollectionExerciseAndSurvey decorator = new CollectionExerciseAndSurvey();
    ActionRequest actionRequest = new ActionRequest();

    ActionRequestContext context =
        createActionRequestContext(SampleUnitDTO.SampleUnitType.H, "SOCIALNOT");

    decorator.decorateActionRequest(actionRequest, context);

    assertThat(actionRequest.getReturnByDate())
        .isEqualTo(
            expectedDateFormat.format(
                context.getCollectionExercise().getScheduledReturnDateTime()));
  }

  @Test
  public void ensureReturnByDateFormattedForSocialICF() {
    SimpleDateFormat expectedDateFormat = new SimpleDateFormat("dd/MM/YYYY");
    CollectionExerciseAndSurvey decorator = new CollectionExerciseAndSurvey();
    ActionRequest actionRequest = new ActionRequest();

    ActionRequestContext context =
        createActionRequestContext(SampleUnitDTO.SampleUnitType.H, "SOCIALICF");

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

    ActionRequestContext context =
        createActionRequestContext(SampleUnitDTO.SampleUnitType.B, "BSNOT");

    decorator.decorateActionRequest(actionRequest, context);

    assertThat(actionRequest.getReturnByDate())
        .isEqualTo(
            expectedDateFormat.format(
                context.getCollectionExercise().getScheduledReturnDateTime()));
  }

  private ActionRequestContext createActionRequestContext(
      SampleUnitDTO.SampleUnitType sampleUnitType, String actionTypeStr) {
    ActionRequestContext context = new ActionRequestContext();
    Date date = new Date();

    CollectionExerciseDTO collectionExercise = new CollectionExerciseDTO();
    collectionExercise.setExerciseRef("123");
    collectionExercise.setUserDescription("Test Description");
    collectionExercise.setScheduledReturnDateTime(new Timestamp(date.getTime()));

    Action action = new Action();
    ActionType actionType = new ActionType();
    actionType.setName(actionTypeStr);
    action.setActionType(actionType);
    context.setAction(action);
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
