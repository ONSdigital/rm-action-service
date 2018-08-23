package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
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
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.Association;
import uk.gov.ons.ctp.response.party.representation.Enrolment;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

/**
 * An ActionService implementation which encapsulates all business logic operating on the Action
 * entity model.
 */
@Service
public class ActionService {
  private static final Logger log = LoggerFactory.getLogger(ActionService.class);

  private static final int TRANSACTION_TIMEOUT = 30;
  private static final String ENABLED = "ENABLED";

  private static final String NO_RESPONDENTS_FOR_SURVEY = "No respondents found for survey";

  private ActionRepository actionRepo;
  private ActionCaseRepository actionCaseRepo;
  private ActionPlanRepository actionPlanRepository;
  private ActionPlanJobRepository actionPlanJobRepository;
  private ActionRuleRepository actionRuleRepo;
  private ActionTypeRepository actionTypeRepo;

  private CollectionExerciseClientService collectionExerciseClientService;
  private PartySvcClientService partySvcClientService;

  private StateTransitionManager<ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  public ActionService(
      ActionRepository actionRepo,
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepository,
      ActionPlanJobRepository actionPlanJobRepository,
      ActionRuleRepository actionRuleRepo,
      ActionTypeRepository actionTypeRepo,
      CollectionExerciseClientService collectionExerciseClientService,
      PartySvcClientService partySvcClientService,
      StateTransitionManager<ActionState, ActionDTO.ActionEvent> actionSvcStateTransitionManager) {
    this.actionRepo = actionRepo;
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepository = actionPlanRepository;
    this.actionPlanJobRepository = actionPlanJobRepository;
    this.actionRuleRepo = actionRuleRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.collectionExerciseClientService = collectionExerciseClientService;
    this.partySvcClientService = partySvcClientService;
    this.actionSvcStateTransitionManager = actionSvcStateTransitionManager;
  }

  @CoverageIgnore
  public List<Action> findAllActionsOrderedByCreatedDateTimeDescending() {
    log.debug("Entering findAllActions");
    return actionRepo.findAllByOrderByCreatedDateTimeDesc();
  }

  @CoverageIgnore
  public List<Action> findActionsByTypeAndStateOrderedByCreatedDateTimeDescending(
      final String actionTypeName, final ActionDTO.ActionState state) {
    log.debug("Entering findActionsByTypeAndState with {} {}", actionTypeName, state);
    return actionRepo.findByActionTypeNameAndStateOrderByCreatedDateTimeDesc(actionTypeName, state);
  }

  @CoverageIgnore
  public List<Action> findActionsByType(final String actionTypeName) {
    log.debug("Entering findActionsByType with {}", actionTypeName);
    return actionRepo.findByActionTypeNameOrderByCreatedDateTimeDesc(actionTypeName);
  }

  @CoverageIgnore
  public List<Action> findActionsByState(final ActionDTO.ActionState state) {
    log.debug("Entering findActionsByState with {}", state);
    return actionRepo.findByStateOrderByCreatedDateTimeDesc(state);
  }

  @CoverageIgnore
  public Action findActionById(final UUID actionId) {
    log.debug("Entering findActionById with {}", actionId);
    return actionRepo.findById(actionId);
  }

  @CoverageIgnore
  public List<Action> findActionsByCaseId(final UUID caseId) {
    log.debug("Entering findActionsByCaseId with {}", caseId);
    return actionRepo.findByCaseIdOrderByCreatedDateTimeDesc(caseId);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public List<Action> cancelActions(final UUID caseId) throws CTPException {
    log.debug("Entering cancelAction with {}", caseId);

    final List<Action> flushedActions = new ArrayList<>();
    final List<Action> actions = actionRepo.findByCaseId(caseId);
    for (final Action action : actions) {
      if (action.getActionType().getCanCancel()) {
        log.debug(
            "Cancelling action {} of type {}", action.getId(), action.getActionType().getName());
        final ActionDTO.ActionState nextState =
            actionSvcStateTransitionManager.transition(
                action.getState(), ActionEvent.REQUEST_CANCELLED);
        action.setState(nextState);
        action.setUpdatedDateTime(DateTimeUtil.nowUTC());
        actionRepo.saveAndFlush(action);
        flushedActions.add(action);
      }
    }
    return flushedActions;
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public Action feedBackAction(final ActionFeedback actionFeedback) throws CTPException {
    final String actionId = actionFeedback.getActionId();
    log.debug("Entering feedBackAction with actionId {}", actionId);

    Action result = null;
    if (!StringUtils.isEmpty(actionId)) {
      result = actionRepo.findById(UUID.fromString(actionId));
      if (result != null) {
        final ActionDTO.ActionEvent event =
            ActionDTO.ActionEvent.valueOf(actionFeedback.getOutcome().name());
        result.setSituation(actionFeedback.getSituation());
        result.setUpdatedDateTime(DateTimeUtil.nowUTC());
        final ActionDTO.ActionState nextState =
            actionSvcStateTransitionManager.transition(result.getState(), event);
        result.setState(nextState);
        result = actionRepo.saveAndFlush(result);
      }
    }

    return result;
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public Action createAdHocAction(final Action action) {
    log.debug("Entering createAdhocAction with {}", action);

    // guard against the caller providing an id - we would perform an update otherwise
    action.setActionPK(null);

    // the incoming action has a placeholder action type with the name as provided to the caller but
    // we need the entire
    // action type object for that action type name
    final ActionType actionType = actionTypeRepo.findByName(action.getActionType().getName());
    action.setActionType(actionType);

    action.setManuallyCreated(true);
    action.setCreatedDateTime(DateTimeUtil.nowUTC());
    action.setState(ActionState.SUBMITTED);
    action.setId(UUID.randomUUID());
    return actionRepo.saveAndFlush(action);
  }

  @Transactional
  public void createScheduledActions(ActionPlan actionPlan, ActionPlanJob actionPlanJob) {
    List<ActionCase> cases = actionCaseRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
    List<ActionRule> rules = actionRuleRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
    cases.forEach(
        caze -> {
          if (isActionPlanLive(caze)) {
            rules.forEach(
                rule -> {
                  if (hasRuleTriggered(rule)) {
                    try {
                      createActions(caze, rule);
                    } catch (Exception ex) {
                      log.with("caseId", caze.getId().toString())
                        .with("cause", ex.getCause())
                        .with("actionRuleId", rule.getId().toString())
                        .with("message", ex.getMessage())
                        .error("Failed to create actions");
                    }
                  }
                });
          }
        });
    updatePlanAndJob(actionPlan, actionPlanJob);
  }

  private boolean isActionPlanLive(ActionCase actionCase) {
    final Timestamp currentTime = new Timestamp((new Date()).getTime());
    return actionCase.getActionPlanStartDate().before(currentTime)
        && actionCase.getActionPlanEndDate().after(currentTime);
  }

  private boolean hasRuleTriggered(ActionRule rule) {
    final Timestamp currentTime = new Timestamp((new Date()).getTime());
    Timestamp triggerDateTime =
        Timestamp.valueOf(
            LocalDateTime.ofInstant(rule.getTriggerDateTime().toInstant(), ZoneOffset.UTC));
    return triggerDateTime.before(currentTime);
  }

  private void createActions(ActionCase actionCase, ActionRule actionRule) {
    ActionType actionType = actionTypeRepo.findByActionTypePK(actionRule.getActionTypeFK());

    if (actionCase.getSampleUnitType().equals(SampleUnitDTO.SampleUnitType.B.toString())
        && (actionType.getActionTypePK() == 5 || actionType.getActionTypePK() == 7)) {
      PartyDTO businessParty =
          partySvcClientService.getParty(actionCase.getSampleUnitType(), actionCase.getPartyId());


      CollectionExerciseDTO collectionExercise =
          collectionExerciseClientService.getCollectionExercise(
              actionCase.getCollectionExerciseId());

      List<Association> enrolledAssociations =
          associationsEnrolledForSurvey(businessParty, collectionExercise.getSurveyId());

      if (enrolledAssociations.isEmpty()) {
        log.error(NO_RESPONDENTS_FOR_SURVEY);
        throw new IllegalStateException(NO_RESPONDENTS_FOR_SURVEY);
      }

      enrolledAssociations.forEach(
          association ->
              createAction(actionCase, actionRule, actionType, UUID.fromString(association.getPartyId())));

    } else {
      createAction(actionCase, actionRule, actionType, actionCase.getPartyId());
    }
  }

  private List<Association> associationsEnrolledForSurvey(PartyDTO party, String surveyId) {
    return party
        .getAssociations()
        .stream()
        .filter(association -> isAssociationEnabledForSurvey(association, surveyId))
        .collect(Collectors.toList());
  }

  private boolean isAssociationEnabledForSurvey(Association association, String surveyId) {
    return association
        .getEnrolments()
        .stream()
        .anyMatch(enrolment -> isEnrolmentEnabledForSurvey(enrolment, surveyId));
  }

  private boolean isEnrolmentEnabledForSurvey(final Enrolment enrolment, String surveyId) {
    return enrolment.getSurveyId().equals(surveyId)
        && enrolment.getEnrolmentStatus().equalsIgnoreCase(ENABLED);
  }

  private void createAction(ActionCase actionCase, ActionRule actionRule, ActionType actionType, UUID partyId) {
    if (actionRepo.findOneByCaseIdAndActionRuleFKAndPartyId(
            actionCase.getId(), actionRule.getActionRulePK(), partyId)
        != null) {
      log.debug("Action already exists");
      return;
    }

    Action newAction = new Action();
    newAction.setId(UUID.randomUUID());
    newAction.setCreatedBy("SYSTEM");
    newAction.setManuallyCreated(false);
    newAction.setState(ActionState.SUBMITTED);
    newAction.setCreatedDateTime(new Timestamp((new Date()).getTime()));

    newAction.setCaseFK(actionCase.getCasePK());
    newAction.setCaseId(actionCase.getId());

    newAction.setActionPlanFK(actionRule.getActionPlanFK());
    newAction.setActionRuleFK(actionRule.getActionRulePK());
    newAction.setActionType(actionType);
    newAction.setPriority(actionRule.getPriority());

    newAction.setPartyId(partyId);

    actionRepo.saveAndFlush(newAction);
  }

  private void updatePlanAndJob(ActionPlan actionPlan, ActionPlanJob actionPlanJob) {
    final Timestamp currentTime = new Timestamp((new Date()).getTime());
    actionPlanJob.complete(currentTime);
    actionPlan.setLastRunDateTime(currentTime);
    actionPlanJobRepository.saveAndFlush(actionPlanJob);
    actionPlanRepository.saveAndFlush(actionPlan);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public Action updateAction(final Action action) {
    final UUID actionId = action.getId();
    log.debug("Entering updateAction with actionId {}", actionId);
    Action existingAction = actionRepo.findById(actionId);
    if (existingAction != null) {
      boolean needsUpdate = false;

      final Integer newPriority = action.getPriority();
      log.debug("newPriority = {}", newPriority);
      if (newPriority != null) {
        needsUpdate = true;
        existingAction.setPriority(newPriority);
      }

      final String newSituation = action.getSituation();
      log.debug("newSituation = {}", newSituation);
      if (newSituation != null) {
        needsUpdate = true;
        existingAction.setSituation(newSituation);
      }

      if (needsUpdate) {
        existingAction.setUpdatedDateTime(DateTimeUtil.nowUTC());
        log.debug("updating action with {}", existingAction);
        existingAction = actionRepo.saveAndFlush(existingAction);
      }
    }
    return existingAction;
  }
}
