package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.action.service.ActionService;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;

/** This is the service class that processes actions */
@Component
public class ActionProcessor {

  private static final Logger log = LoggerFactory.getLogger(ActionProcessor.class);

  public static final String NOTIFY = "Notify";

  public static final String PRINTER = "Printer";

  private ActionService actionService;
  private ActionTypeService actionTypeService;

  private ActionProcessingService actionProcessingService;

  public ActionProcessor(
      ActionService actionService,
      ActionTypeService actionTypeService,
      ActionProcessingService actionProcessingService) {
    this.actionService = actionService;
    this.actionTypeService = actionTypeService;
    this.actionProcessingService = actionProcessingService;
  }

  public void processEmails() {
    List<ActionType> actionTypes = actionTypeService.findByHandler(NOTIFY);
    if (actionTypes.isEmpty()) {
      log.warn("no action types to process");
    }
    for (ActionType actionType : actionTypes) {
      log.with("type", actionType.getName()).info("processing actionType");
      List<Integer> actionRules = actionService.getActionRules(actionType);
      for (Integer actionRule : actionRules) {
        log.with("actionRule", actionRule).info("processing action rule");
        processEmailsForActionRule(actionType, actionRule);
      }
    }
  }

  protected void processEmailsForActionRule(ActionType actionType, Integer actionRuleFK) {
    log.with("actionRule", actionRuleFK)
        .with("actionType", actionType.getName())
        .info("process emails for action rule");
    List<Action> allActions = actionService.getSubmittedActions(actionType, actionRuleFK);
    if (!allActions.isEmpty()) {
      log.with("actions", allActions.size()).info("found actions to process");
      actionProcessingService.processEmails(actionType, allActions);
    }
  }

  public void processLetters() {
    List<ActionType> actionTypes = actionTypeService.findByHandler(PRINTER);
    if (actionTypes.isEmpty()) {
      log.warn("no action types to process");
    }
    for (ActionType actionType : actionTypes) {
      log.with("type", actionType.getName()).info("processing actionType");
      List<Integer> actionRules = actionService.getActionRules(actionType);
      for (Integer actionRule : actionRules) {
        log.with("actionRule", actionRule).info("processing action rule");
        processLettersForActionRule(actionType, actionRule);
      }
    }
  }

  protected void processLettersForActionRule(ActionType actionType, Integer actionRuleFK) {
    log.with("actionRule", actionRuleFK)
        .with("actionType", actionType.getName())
        .info("process letters for action rule");
    List<Action> allActions = actionService.getSubmittedActions(actionType, actionRuleFK);
    if (!allActions.isEmpty()) {
      log.with("actions", allActions.size()).info("found actions to process");
      actionProcessingService.processLetters(actionType, allActions);
    }
  }
}
