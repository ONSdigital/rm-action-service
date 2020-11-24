package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.scheduled.export.ExportProcessor;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.action.service.NotifyService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;

/** This is the service class that distributes actions to downstream services */
@Component
public class ActionDistributor {

  private static final Logger log = LoggerFactory.getLogger(ActionDistributor.class);

  public static final String NOTIFY = "Notify";
  public static final String PRINTER = "Printer";

  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;
  private static final Set<ActionState> ACTION_STATES_TO_GET =
      Sets.immutableEnumSet(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED);

  private ActionRepository actionRepo;
  private ActionCaseRepository actionCaseRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService businessActionProcessingService;

  private ExportProcessor exportProcessor;

  private NotifyService notifyService;

  private StateTransitionManager<ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  public ActionDistributor(
      AppConfig appConfig,
      ActionRepository actionRepo,
      ActionCaseRepository actionCaseRepo,
      ActionTypeRepository actionTypeRepo,
      @Qualifier("business") ActionProcessingService businessActionProcessingService,
      StateTransitionManager<ActionState, ActionDTO.ActionEvent> actionSvcStateTransitionManager,
      ExportProcessor exportProcessor,
      NotifyService notifyService) {
    this.actionRepo = actionRepo;
    this.actionCaseRepo = actionCaseRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.businessActionProcessingService = businessActionProcessingService;
    this.actionSvcStateTransitionManager = actionSvcStateTransitionManager;
    this.exportProcessor = exportProcessor;
    this.notifyService = notifyService;
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
    Stream<Action> stream = actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET);
    List<Action> allActions = stream.collect(Collectors.toList());

    if (actionType.getHandler().equals(NOTIFY)) {
      for (Action action : allActions) {
        if (checkCaseExists(action)) {
          transitionAction(action, actionType);
          // send to pub sub
          List<ActionRequest> actionRequests =
            businessActionProcessingService.prepareActionRequests(action);
          for (ActionRequest actionRequest : actionRequests) {
            notifyService.processNotification(actionRequest);
          }
        }
      }
    } else if (actionType.getHandler().equals(PRINTER)) {
      // action requests are decorated actions
      List<ActionRequest> printerActions = new ArrayList<>();
      for (Action action : allActions) {
        if (checkCaseExists(action)) {
          transitionAction(action, actionType);
          List<ActionRequest> actionRequests =
              businessActionProcessingService.prepareActionRequests(action);
          printerActions.addAll(actionRequests);
        }
      }
      //TODO remove this export processor to print file processor
      exportProcessor.export(printerActions);
    } else {
      log.with("handler", actionType.getHandler()).warn("unsupported action type handler");
    }
  }

  private boolean checkCaseExists(final Action action) {
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
      return false;
    }
    return true;
  }

  /**
   * Change the action status in db to indicate we have sent this action downstream, and clear
   * previous situation (in the scenario where the action has prev. failed)
   *
   * @param action the action to change and persist
   * @param actionType the action type
   * @throws CTPException if action state transition error
   */
  private void transitionAction(final Action action, final ActionType actionType) {

    ActionDTO.ActionEvent event =
        actionType.getResponseRequired()
            ? ActionDTO.ActionEvent.REQUEST_DISTRIBUTED
            : ActionDTO.ActionEvent.REQUEST_COMPLETED;

    ActionDTO.ActionState nextState = null;

    try {
      nextState = actionSvcStateTransitionManager.transition(action.getState(), event);
    } catch (CTPException ctpExeption) {
      throw new IllegalStateException(ctpExeption);
    }

    action.setState(nextState);
    action.setSituation(null);
    action.setUpdatedDateTime(DateTimeUtil.nowUTC());
    actionRepo.saveAndFlush(action);
  }
}
