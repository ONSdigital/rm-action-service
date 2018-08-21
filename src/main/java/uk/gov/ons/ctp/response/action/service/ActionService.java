package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;

/**
 * An ActionService implementation which encapsulates all business logic operating on the Action
 * entity model.
 */
@Service
public class ActionService {
  private static final Logger log = LoggerFactory.getLogger(ActionService.class);

  private static final int TRANSACTION_TIMEOUT = 30;

  @Autowired private ActionRepository actionRepo;

  @Autowired private ActionCaseRepository actionCaseRepository;

  @Autowired private ActionTypeRepository actionTypeRepo;

  @Autowired private ActionPlanRepository actionPlanRepository;

  @Autowired private ActionPlanJobRepository actionPlanJobRepository;

  @Autowired
  private StateTransitionManager<ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

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
  public Action findActionByActionPK(final BigInteger actionKey) {
    log.debug("Entering findActionByActionPK with {}", actionKey);
    return actionRepo.findOne(actionKey);
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
  public Action createAction(final Action action) {
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
  public void createScheduledActions(Integer actionPlanJobId) {
    final ActionPlanJob actionPlanJob =
        actionPlanJobRepository.findByActionPlanJobPK(actionPlanJobId);

    if (actionPlanJob == null) {
      return;
    }

    final ActionPlan actionPlan =
        actionPlanRepository.findByActionPlanPK(actionPlanJob.getActionPlanFK());
    final Timestamp currentTime = new Timestamp((new Date()).getTime());

    actionRepo
        .findPotentialActionsActiveDate(actionPlan.getActionPlanPK(), currentTime)
        .stream()
        .map(pa -> Action.fromPotentialAction(pa, currentTime))
        .forEach(a -> actionRepo.save(a));
    actionRepo.flush();

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
