package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanSelector;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The service for ActionPlans
 */
public interface ActionPlanService extends CTPService {

  /**
   * This method returns all action plans.
   *
   * @return List<ActionPlan> This returns all action plans.
   */
  List<ActionPlan> findActionPlans();

  /**
   * This method returns all action plans with the given selectors
   *
   * @return List<ActionPlan> This returns all action plans.
   */
  List<ActionPlan> findActionPlansBySelectors(Map<String, String> selectors);

  /**
   * This method returns the action plan for the specified action plan primary key.
   *
   * @param actionPlanKey This is the action plan primary key
   * @return ActionPlan This returns the associated action plan.
   */
  ActionPlan findActionPlan(Integer actionPlanKey);

  /**
   * This method returns the action plan for the specified action plan id.
   *
   * @param actionPlanId This is the action plan id
   * @return ActionPlan This returns the associated action plan.
   */
  ActionPlan findActionPlanById(UUID actionPlanId);

  /**
   * This method returns the action plan for the specified action plan name.
   *
   * @param name This is the action plan name
   * @return ActionPlan This returns the associated action plan.
   */
  ActionPlan findActionPlanByName(String name);

  /**
   * This method returns the action plan after it has been updated. Note that only the description and
   * the lastGoodRunDatetime can be updated.
   *
   * @param actionPlanId This is the action plan id of the action plan to be updated
   * @param actionPlan   This is the action plan containing the potentially new description and lastGoodRunDatetime
   * @return ActionPlan This returns the updated action plan.
   */
  ActionPlan updateActionPlan(UUID actionPlanId, ActionPlan actionPlan, ActionPlanSelector actionPlanSelector);

  /**
   * This method returns the action plan after it has been created.
   *
   * @param actionPlan action plan to be created
   * @param actionPlanSelector selectors for the given action plan
   * @return ActionPlan This returns the created action plan.
   */
  ActionPlanDTO createActionPlan(ActionPlan actionPlan, ActionPlanSelector actionPlanSelector);
}
