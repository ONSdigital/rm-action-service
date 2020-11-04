package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO;

/** This is the service class that distributes actions to downstream services */
@Component
public class ActionDistributor {

  private static final Logger log = LoggerFactory.getLogger(ActionDistributor.class);

  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;
  private static final Set<ActionState> ACTION_STATES_TO_GET =
      Sets.immutableEnumSet(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED);

  private AppConfig appConfig;

  private ActionRepository actionRepo;
  private ActionCaseRepository actionCaseRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService businessActionProcessingService;

  private StateTransitionManager<ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  public ActionDistributor(
      AppConfig appConfig,
      ActionRepository actionRepo,
      ActionCaseRepository actionCaseRepo,
      ActionTypeRepository actionTypeRepo,
      @Qualifier("business") ActionProcessingService businessActionProcessingService,
      StateTransitionManager<ActionState, ActionDTO.ActionEvent> actionSvcStateTransitionManager) {
    this.appConfig = appConfig;
    this.actionRepo = actionRepo;
    this.actionCaseRepo = actionCaseRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.businessActionProcessingService = businessActionProcessingService;
    this.actionSvcStateTransitionManager = actionSvcStateTransitionManager;
  }

  /**
   * Called on schedule to check for submitted actions then creates and distributes requests to
   * action exporter or notify gateway
   */
  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void distribute() {
    List<ActionType> actionTypes = actionTypeRepo.findAll();
    actionTypes.forEach(this::processActionType);
  }

  private void processActionType(final ActionType actionType) {
    log.with("type", actionType.getName()).trace("Processing actionType");
    try (Stream<Action> actions =
        actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET)) {
      actions.forEach(this::processAction);
    }
  }

  private void processAction(Action action) {
    try {
      log.with("action_id", action.getId().toString()).info("Processing action");
      ActionProcessingService ap = getActionProcessingService(action);

      if (ap == null) {
        // Case no longer exists for action, has been set to REQUEST_CANCELLED
        log.with("action_id", action.getId().toString()).info("Skipping action without case");
        return;
      }

      if (action.getState().equals(ActionState.SUBMITTED)) {
        ap.processActionRequests(action.getId());
      } else if (action.getState().equals(ActionState.CANCEL_SUBMITTED)) {
        ap.processActionCancel(action.getId());
      }
    } catch (Exception ex) {
      // We intentionally catch all exceptions here.
      // If one action fails to process we still want to try and process the remaining actions
      log.with("action", action)
          .error("Failed to process action. Will be retried at next schedule", ex);
    }
  }

  private ActionProcessingService getActionProcessingService(Action action) {
    ActionCase actionCase = actionCaseRepo.findById(action.getCaseId());

    if (actionCase == null) {
      log.with("action", action).info("Case no longer exists for action");
      ActionState newActionState;
      try {
        newActionState =
            actionSvcStateTransitionManager.transition(
                action.getState(), ActionEvent.REQUEST_CANCELLED);
      } catch (CTPException ex) {
        throw new IllegalStateException(ex);
      }

      action.setState(newActionState);
      actionRepo.saveAndFlush(action);
      return null;
    }

    SampleUnitDTO.SampleUnitType caseType =
        SampleUnitDTO.SampleUnitType.valueOf(actionCase.getSampleUnitType());

    switch (caseType) {
      case B:
      case BI:
        return businessActionProcessingService;

      default:
        throw new UnsupportedOperationException("Sample Type: " + caseType + " is not supported!");
    }
  }
}
