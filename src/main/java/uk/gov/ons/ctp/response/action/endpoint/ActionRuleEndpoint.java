package uk.gov.ons.ctp.response.action.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.representation.ActionRuleDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.service.ActionRuleService;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;

import java.util.List;
import java.util.UUID;


/**
 * The REST endpoint controller for Action Rules.
 */
@RestController
@RequestMapping(value = "/actionrules", produces = "application/json")
@Slf4j
public class ActionRuleEndpoint implements CTPEndpoint {

  public static final String ACTION_PLAN_NOT_FOUND = "ActionPlan with id %s not found";

  @Autowired
  private ActionRuleService actionRuleService;

  @Autowired
  private ActionTypeService actionTypeService;

  @Autowired
  private ActionPlanService actionPlanService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;


  /**
   * GET ActionRules for the specified ActionPlan Id.
   *
   * @param actionPlanId actionPlanID to which action rules apply
   * @return List<ActionRulesDTO> Returns the associated action rules for the specified action plan id
   */
  @RequestMapping(value = "/actionplan/{actionplanid}", method = RequestMethod.GET)
  public ResponseEntity<List<ActionRuleDTO>> findActionRulesByActionPlanId(@PathVariable("actionplanid") final UUID actionPlanId)
          throws CTPException {
    log.info("Entering findActionRulesByActionPlanId...");

    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }

    final List<ActionRule> actionRules = actionRuleService.findActionRulesByActionPlanId(actionPlanId);

    return ResponseEntity.ok(buildActionRulesDTOs(actionRules));
  }

  /**
   * To build a list of ActionRuleDTOs from ActionRules populating the actionTypeName
   *
   * @param actionRules a list of ActionRules
   * @return a list of ActionRuleDTOs
   */
  private List<ActionRuleDTO> buildActionRulesDTOs(final List<ActionRule> actionRules) {
    final List<ActionRuleDTO> actionRulesDTOs = mapperFacade.mapAsList(actionRules, ActionRuleDTO.class);

    int index = 0;
    for (final ActionRule actionRule : actionRules) {
      final int actionTypeFK = actionRule.getActionTypeFK();
      final String actionTypeName = actionTypeService.findActionType(actionTypeFK).getName();
      actionRulesDTOs.get(index).setActionTypeName(actionTypeName);
      index = index + 1;
    }

    return actionRulesDTOs;
  }

}


