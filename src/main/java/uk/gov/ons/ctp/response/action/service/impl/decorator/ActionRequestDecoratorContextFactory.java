package uk.gov.ons.ctp.response.action.service.impl.decorator;

import uk.gov.ons.ctp.response.action.domain.model.Action;

public interface ActionRequestDecoratorContextFactory {

  ActionRequestDecoratorContext getActionRequestDecoratorContext(Action action);
}
