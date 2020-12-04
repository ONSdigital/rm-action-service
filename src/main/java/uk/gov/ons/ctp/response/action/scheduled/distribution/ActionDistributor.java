package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;

/** This is the service class that distributes actions to downstream services */
@Component
public class ActionDistributor {

  private static final Logger log = LoggerFactory.getLogger(ActionDistributor.class);

  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;
  private static final Set<ActionState> ACTION_STATES_TO_GET =
      Sets.immutableEnumSet(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED);

  public static final String NOTIFY = "Notify";

  public static final String PRINTER = "Printer";

  private ActionRepository actionRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService actionProcessingService;

  public ActionDistributor(
      ActionRepository actionRepo,
      ActionTypeRepository actionTypeRepo,
      ActionProcessingService actionProcessingService) {
    this.actionRepo = actionRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.actionProcessingService = actionProcessingService;
  }

  /**
   * Once an action plan has executed it will have created all the require actions for that event.
   * This job will then process the actions and either send emails or produce a print file for the
   * letters.
   */
  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void distribute() {
    List<ActionType> actionTypes = actionTypeRepo.findAll();
    actionTypes.forEach(this::processActionType);
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void processEmails() {
    List<ActionType> actionTypes = actionTypeRepo.findByHandler(NOTIFY);
    for (ActionType actionType : actionTypes) {
      Stream<Action> stream =
          actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET);
      List<Action> allActions = stream.collect(Collectors.toList());
      if (!allActions.isEmpty()) {
        actionProcessingService.processEmails(actionType, allActions);
      }
    }
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void processLetters() {
    List<ActionType> actionTypes = actionTypeRepo.findByHandler(PRINTER);
    for (ActionType actionType : actionTypes) {
      Stream<Action> stream =
          actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET);
      List<Action> allActions = stream.collect(Collectors.toList());
      if (!allActions.isEmpty()) {
        actionProcessingService.processLetters(actionType, allActions);
      }
    }
  }

  private void processActionType(final ActionType actionType) {
    log.with("type", actionType.getName()).trace("Processing actionType");
    Stream<Action> stream = actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET);
    List<Action> allActions = stream.collect(Collectors.toList());
    if (!allActions.isEmpty()) {
      actionProcessingService.processActions(actionType, allActions);
    }
  }
}
