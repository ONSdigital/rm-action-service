package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionRequestDecoratorContext;

public interface ActionRequestDecorator {

  void decorateActionRequest(ActionRequest actionRequest, ActionRequestDecoratorContext context);
}
