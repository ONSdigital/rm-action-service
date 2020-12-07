package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;

/** This is the service class that processes actions */
@Component
public class ActionProcessor {

  private static final Logger log = LoggerFactory.getLogger(ActionProcessor.class);

  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;
  private static final Set<ActionState> ACTION_STATES_TO_GET =
      Sets.immutableEnumSet(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED);

  public static final String NOTIFY = "Notify";

  public static final String PRINTER = "Printer";

  private ActionRepository actionRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService actionProcessingService;

  public ActionProcessor(
      ActionRepository actionRepo,
      ActionTypeRepository actionTypeRepo,
      ActionProcessingService actionProcessingService) {
    this.actionRepo = actionRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.actionProcessingService = actionProcessingService;
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void processEmails() {
    List<ActionType> actionTypes = actionTypeRepo.findByHandler(NOTIFY);
    for (ActionType actionType : actionTypes) {
      log.with("type", actionType.getName()).trace("Processing actionType");
      List<Integer> actionRules =
          actionRepo.findDistinctActionRuleFKByActionTypeAndStateIn(
              actionType, ACTION_STATES_TO_GET);
      for (Integer actionRule : actionRules) {
        log.with("actionRule", actionRule).debug("processing action rule");
        processEmailsForActionRule(actionType, actionRule);
      }
    }
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS, propagation = Propagation.REQUIRES_NEW)
  protected void processEmailsForActionRule(ActionType actionType, Integer actionRuleFK) {
    Stream<Action> stream =
        actionRepo.findByActionTypeAndActionRuleFKAndStateIn(
            actionType, actionRuleFK, ACTION_STATES_TO_GET);
    List<Action> allActions = stream.collect(Collectors.toList());
    if (!allActions.isEmpty()) {
      actionProcessingService.processEmails(actionType, allActions);
    }
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void processLetters() {
    List<ActionType> actionTypes = actionTypeRepo.findByHandler(PRINTER);
    for (ActionType actionType : actionTypes) {
      log.with("type", actionType.getName()).trace("Processing actionType");
      List<Integer> actionRules =
          actionRepo.findDistinctActionRuleFKByActionTypeAndStateIn(
              actionType, ACTION_STATES_TO_GET);
      for (Integer actionRule : actionRules) {
        log.with("actionRule", actionRule).debug("processing action rule");
        processLettersForActionRule(actionType, actionRule);
      }
    }
  }

  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS, propagation = Propagation.REQUIRES_NEW)
  protected void processLettersForActionRule(ActionType actionType, Integer actionRuleFK) {
    Stream<Action> stream =
        actionRepo.findByActionTypeAndActionRuleFKAndStateIn(
            actionType, actionRuleFK, ACTION_STATES_TO_GET);
    List<Action> allActions = stream.collect(Collectors.toList());
    if (!allActions.isEmpty()) {
      actionProcessingService.processLetters(actionType, allActions);
    }
  }
}
