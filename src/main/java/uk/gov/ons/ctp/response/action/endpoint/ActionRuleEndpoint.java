package uk.gov.ons.ctp.response.action.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.representation.ActionRuleDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRulePostRequestDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRulePutRequestDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.service.ActionRuleService;
import uk.gov.ons.ctp.response.action.service.ActionTypeService;

@Api("API for action rules")
@RestController
@RequestMapping(value = "/actionrules", produces = "application/json")
@Slf4j
public class ActionRuleEndpoint implements CTPEndpoint {

  static final String ACTION_PLAN_NOT_FOUND = "ActionPlan with id %s not found";
  static final String ACTION_TYPE_NOT_FOUND = "ActionType with name %s not found";
  static final String ACTION_RULE_NOT_FOUND = "ActionRule with id %s not found";

  @Autowired private ActionRuleService actionRuleService;

  @Autowired private ActionTypeService actionTypeService;

  @Autowired private ActionPlanService actionPlanService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  @ApiOperation(value = "List action rules for an actionPlanId")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 2 LINES
    @ApiResponse(code = 200, message = "Action rules for an actionPlanId"),
    @ApiResponse(code = 404, message = "Action plan not found"),
  })
  @RequestMapping(value = "/actionplan/{actionplanid}", method = RequestMethod.GET)
  public ResponseEntity<List<ActionRuleDTO>> findActionRulesByActionPlanId(
      @PathVariable("actionplanid") final UUID actionPlanId) throws CTPException {
    log.info("Entering findActionRulesByActionPlanId...");

    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }

    final List<ActionRule> actionRules =
        actionRuleService.findActionRulesByActionPlanId(actionPlanId);

    return ResponseEntity.ok(buildActionRulesDTOs(actionRules));
  }

  @ApiOperation(value = "Create an action rule")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 201, message = "Action rule has been created"),
    @ApiResponse(code = 404, message = "Action plan or action type not found"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
  })
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ActionRuleDTO> createActionRule(
      final @RequestBody @Valid ActionRulePostRequestDTO actionRulePostRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.debug(
        "Entering createActionRule with actionRulePostRequestDTO {}", actionRulePostRequestDTO);
    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for create action rule: ", bindingResult);
    }

    final UUID actionPlanId = actionRulePostRequestDTO.getActionPlanId();
    final ActionPlan actionPlan = actionPlanService.findActionPlanById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, ACTION_PLAN_NOT_FOUND, actionPlanId);
    }

    final String actionTypeName = actionRulePostRequestDTO.getActionTypeName();
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
    actionRuleDTO.setActionTypeName(actionType.getName());

    final String newResourceUrl =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(actionRuleDTO.getId())
            .toUri()
            .toString();
    return ResponseEntity.created(URI.create(newResourceUrl)).body(actionRuleDTO);
  }

  @ApiOperation(value = "Update an action rule")
  @ApiResponses({
    // CHECKSTYLE IGNORE indentation FOR NEXT 3 LINES
    @ApiResponse(code = 201, message = "Action rule has been updated"),
    @ApiResponse(code = 404, message = "Action rule not found"),
    @ApiResponse(code = 400, message = "Required fields are missing or invalid"),
  })
  @RequestMapping(
      value = "/{actionRuleId}",
      method = RequestMethod.PUT,
      consumes = "application/json")
  public ActionRuleDTO updateActionRule(
      @PathVariable("actionRuleId") final UUID actionRuleId,
      @RequestBody(required = false) @Valid final ActionRulePutRequestDTO actionRulePutRequestDTO,
      final BindingResult bindingResult)
      throws CTPException, InvalidRequestException {
    log.info("Updating Action Rule with {} - {}", actionRuleId, actionRulePutRequestDTO);
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
    final String actionTypeName =
        actionTypeService.findActionType(updatedActionRule.getActionTypeFK()).getName();
    resultDTO.setActionTypeName(actionTypeName);
    return resultDTO;
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
      final String actionTypeName = actionTypeService.findActionType(actionTypeFK).getName();
      actionRulesDTOs.get(index).setActionTypeName(actionTypeName);
      index++;
    }

    return actionRulesDTOs;
  }
}
