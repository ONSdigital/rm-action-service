package uk.gov.ons.ctp.response.action.service.impl.decorator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;
import uk.gov.ons.ctp.response.action.service.ActionRequestDecorator;

@Slf4j
public class ActionActionRequestDecorator implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(
      ActionRequest actionRequest, ActionRequestDecoratorContext context) {
    final String actionID = context.getAction().getId().toString();
    log.debug("actionID is {}", actionID);

    actionRequest.setActionId(actionID);
    actionRequest.setActionType(context.getAction().getActionType().getName());
    actionRequest.setResponseRequired(context.getAction().getActionType().getResponseRequired());

    actionRequest.setActionPlan(context.getActionPlan().getId().toString());

    actionRequest.setPriority(
        Priority.fromValue(
            Action.ActionPriority.valueOf(context.getAction().getPriority()).getName()));
  }
}
