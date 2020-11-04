package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.representation.ActionRuleDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRulePostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRulePutRequestDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.service.ActionRuleService;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.error.InvalidRequestException;

/** The REST endpoint controller for Action Rules. */
@RestController
@RequestMapping(value = "/actionrules", produces = "application/json")
public class ActionRuleEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionRuleEndpoint.class);

  public static final String ACTION_PLAN_NOT_FOUND = "ActionPlan with id %s not found";
  public static final String ACTION_TYPE_NOT_FOUND = "ActionType with name %s not found";
  public static final String ACTION_RULE_NOT_FOUND = "ActionRule with id %s not found";

  @Autowired private ActionRuleService actionRuleService;

  @Autowired private ActionTypeService actionTypeService;

  @Autowired private ActionPlanService actionPlanService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * GET ActionRules for the specified ActionPlan Id.
   *
   * @param actionPlanId actionPlanID to which action rules apply
   * @return List<ActionRulesDTO> Returns the associated action rules for the specified action plan
   *     id
   */
  @RequestMapping(value = "/actionplan/{actionplanid}", method = RequestMethod.GET)
  public ResponseEntity<List<ActionRuleDTO>> findActionRulesByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId) throws CTPException {
    log.with("action_plan_id", actionPlanId).debug("Entering findActionRulesByActionPlanId...");

    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }

    final List<ActionRule> actionRules =
        actionRuleService.findActionRulesByActionPlanFK(actionPlan.getActionPlanPK());

    return ResponseEntity.ok(buildActionRulesDTOs(actionRules));
  }

  /**
   * POST Create an ActionRule.
   *
   * @param actionRulePostRequestDTO Incoming ActionDTO with details to validate and from which to
   *     create ActionRule
   * @param bindingResult collects errors thrown by update
   * @return ActionDTO Created Action
   * @throws CTPException on failure to create Action
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ActionRuleDTO> createActionRule(
      final @RequestBody @Valid ActionRulePostRequestDTO actionRulePostRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("action_rule_post_request", actionRulePostRequestDTO)
        .debug("Entering createActionRule");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for create action rule: ", bindingResult);
    }

    final UUID actionPlanId = actionRulePostRequestDTO.getActionPlanId();
    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }

    final String actionTypeName = actionRulePostRequestDTO.getActionTypeName().toString();
    final ActionType actionType = actionTypeService.findActionTypeByName(actionTypeName);
    if (actionType == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_TYPE_NOT_FOUND, actionTypeName);
    }

    ActionRule actionRule = mapperFacade.map(actionRulePostRequestDTO, ActionRule.class);
    actionRule.setActionPlanFK(actionPlan.getActionPlanPK());
    actionRule.setActionTypeFK(actionType.getActionTypePK());
    actionRule = actionRuleService.createActionRule(actionRule);

    final ActionRuleDTO actionRuleDTO = mapperFacade.map(actionRule, ActionRuleDTO.class);
    actionRuleDTO.setActionTypeName(actionType.getActionTypeNameEnum());

    final String newResourceUrl =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(actionRuleDTO.getId())
            .toUri()
            .toString();
    return ResponseEntity.created(URI.create(newResourceUrl)).body(actionRuleDTO);
  }

  /**
   * PUT to update the specified Action Rule.
   *
   * @param actionRuleId Id of the Action Rule to update
   * @param actionRulePutRequestDTO Incoming ActionRuleDTO with details to update
   * @param bindingResult collects errors thrown by update
   * @return ActionRuleDTO Returns the updated Action Rule details
   * @throws CTPException if update operation fails
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(
      value = "/{actionRuleId}",
      method = RequestMethod.PUT,
      consumes = "application/json")
  public ActionRuleDTO updateActionRule(
      @PathVariable("actionRuleId") final UUID actionRuleId,
      @RequestBody(required = false) @Valid final ActionRulePutRequestDTO actionRulePutRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.with("action_rule_id", actionRuleId)
        .with("action_rule_put_request", actionRulePutRequestDTO)
        .debug("Updating Action Rule");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for update action rule: ", bindingResult);
    }

    ActionRule actionRuleToUpdate = mapperFacade.map(actionRulePutRequestDTO, ActionRule.class);
    actionRuleToUpdate.setId(actionRuleId);
    final ActionRule updatedActionRule = actionRuleService.updateActionRule(actionRuleToUpdate);
    if (updatedActionRule == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_RULE_NOT_FOUND, actionRuleId);
    }

    final ActionRuleDTO resultDTO = mapperFacade.map(updatedActionRule, ActionRuleDTO.class);
    final ActionType actionType =
        actionTypeService.findActionType(updatedActionRule.getActionTypeFK());
    resultDTO.setActionTypeName(actionType.getActionTypeNameEnum());
    return resultDTO;
  }

  /**
   * DELETE to delete the specified Action Rule.
   *
   * @param actionRuleId Id of the Action Rule to update
   * @param actionRuleDeleteRequestDTO Incoming ActionRuleDTO with details to update
   * @param bindingResult collects errors thrown by update
   * @return ActionRuleDTO Returns the updated Action Rule details
   * @throws CTPException if delete operation fails
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(
      value = "/{actionRuleId}",
      method = RequestMethod.DELETE,
      consumes = "application/json")
  public ResponseEntity deleteActionRule(
      @PathVariable("actionRuleId") final UUID actionRuleId,
      @RequestBody(required = false) @Valid
          final ActionRulePutRequestDTO actionRuleDeleteRequestDTO,
      final BindingResult bindingResult)
      throws InvalidRequestException, CTPException {
    log.with("action_rule_id", actionRuleId)
        .with("action_rule_delete_request", actionRuleDeleteRequestDTO)
        .info("deleting Action Rule");
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for update action rule: ", bindingResult);
    }
    ActionRule actionRuleToUpdate = mapperFacade.map(actionRuleDeleteRequestDTO, ActionRule.class);
    actionRuleToUpdate.setId(actionRuleId);
    Boolean isSuccess = actionRuleService.deleteActionRule(actionRuleToUpdate);
    if (!isSuccess) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_RULE_NOT_FOUND, actionRuleId);
    }
    return new ResponseEntity(HttpStatus.ACCEPTED);
  }

  /**
   * To build a list of ActionRuleDTOs from ActionRules populating the actionTypeName
   *
   * @param actionRules a list of ActionRules
   * @return a list of ActionRuleDTOs
   */
  private List<ActionRuleDTO> buildActionRulesDTOs(final List<ActionRule> actionRules) {
    final List<ActionRuleDTO> actionRulesDTOs =
        mapperFacade.mapAsList(actionRules, ActionRuleDTO.class);

    int index = 0;
    for (final ActionRule actionRule : actionRules) {
      final int actionTypeFK = actionRule.getActionTypeFK();
      final ActionType actionType = actionTypeService.findActionType(actionTypeFK);
      actionRulesDTOs.get(index).setActionTypeName(actionType.getActionTypeNameEnum());
      index++;
    }

    return actionRulesDTOs;
  }
}
