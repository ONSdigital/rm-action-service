package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;


/**
 * The service for ActionTypes
 */
public interface ActionTypeService extends CTPService {

  /**
   * This method returns the action type for the specified action type primary key.
   *
   * @param actionTypeKey This is the action plan primary key
   * @return ActionType This returns the associated action type.
   */
  ActionType findActionType(Integer actionTypeKey);

}
