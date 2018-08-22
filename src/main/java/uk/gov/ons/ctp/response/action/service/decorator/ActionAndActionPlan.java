package uk.gov.ons.ctp.response.action.service.decorator;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import uk.gov.ons.ctp.response.action.domain.model.Action.ActionPriority;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;

public class ActionAndActionPlan implements ActionRequestDecorator {
  private static final Logger log = LoggerFactory.getLogger(ActionAndActionPlan.class);

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
