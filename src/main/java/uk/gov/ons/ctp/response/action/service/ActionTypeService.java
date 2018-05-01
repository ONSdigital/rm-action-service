package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;


/**
 * The service for ActionTypes
 */
public interface ActionTypeService extends CTPService {

  /**
   * This method returns the action plan for the specified action plan primary key.
   *
   * @param actionTypeKey This is the action plan primary key
   * @return ActionPlan This returns the associated action plan.
   */
  ActionType findActionType(Integer actionTypeKey);

}
