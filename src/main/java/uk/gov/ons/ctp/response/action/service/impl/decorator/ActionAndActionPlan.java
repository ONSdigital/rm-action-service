package uk.gov.ons.ctp.response.action.service.impl.decorator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.domain.model.Action.ActionPriority;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;
import uk.gov.ons.ctp.response.action.service.impl.decorator.context.ActionRequestContext;

@Slf4j
public class ActionAndActionPlan implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    final String actionID = context.getAction().getId().toString();
    log.debug("actionID is {}", actionID);

    actionRequest.setActionId(actionID);
    actionRequest.setActionType(context.getAction().getActionType().getName());
    actionRequest.setResponseRequired(context.getAction().getActionType().getResponseRequired());

    actionRequest.setActionPlan(context.getActionPlan().getId().toString());

    actionRequest.setPriority(
        Priority.fromValue(ActionPriority.valueOf(context.getAction().getPriority()).getName()));
  }
}
