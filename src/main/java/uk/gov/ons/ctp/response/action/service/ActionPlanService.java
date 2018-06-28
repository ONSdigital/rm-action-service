package uk.gov.ons.ctp.response.action.service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;

/** The service for ActionPlans */
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
   * @param selectors HashMap of selectors used to find matching action plans
   * @return List<ActionPlan> This returns all action plans.
   */
  List<ActionPlan> findActionPlansBySelectors(HashMap<String, String> selectors);

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
   * This method returns the action plan after it has been updated. Note that only the description
   * and the lastGoodRunDatetime can be updated.
   *
   * @param actionPlanId This is the action plan id of the action plan to be updated
   * @param actionPlan This is the action plan containing the potentially new description and
   *     lastGoodRunDatetime
   * @return ActionPlan This returns the updated action plan.
   */
  ActionPlan updateActionPlan(UUID actionPlanId, ActionPlan actionPlan);

  /**
   * This method returns the action plan after it has been created.
   *
   * @param actionPlan action plan to be created
   * @return ActionPlan This returns the created action plan
   */
  ActionPlan createActionPlan(ActionPlan actionPlan);
}
