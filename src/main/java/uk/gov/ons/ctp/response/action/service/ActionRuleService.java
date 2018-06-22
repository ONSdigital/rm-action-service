package uk.gov.ons.ctp.response.action.service;

import java.util.List;
import java.util.UUID;
import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;

/** The service for ActionRules */
public interface ActionRuleService extends CTPService {

  /**
   * This method returns all action rules associated to given action plan id
   *
   * @return List<ActionRule> This returns all action rules for action plan id
   */
  List<ActionRule> findActionRulesByActionPlanId(UUID actionPlanId);

  /**
   * This method returns the action rule after it has been created.
   *
   * @param actionRule This is the new action rule to be created.
   * @return ActionRule This returns the created action rule.
   */
  ActionRule createActionRule(ActionRule actionRule);

  /**
   * Update an action rule.
   *
   * @param actionRule ActionRule with update information
   * @return ActionRule Returns updated Action Rule.
   */
  ActionRule updateActionRule(ActionRule actionRule);
}
