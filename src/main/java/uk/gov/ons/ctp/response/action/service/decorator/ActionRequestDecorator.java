package uk.gov.ons.ctp.response.action.service.decorator;

import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;

public interface ActionRequestDecorator {

  void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context);
}
