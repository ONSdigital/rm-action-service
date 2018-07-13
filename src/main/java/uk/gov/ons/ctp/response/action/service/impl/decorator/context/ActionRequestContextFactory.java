package uk.gov.ons.ctp.response.action.service.impl.decorator.context;

import uk.gov.ons.ctp.response.action.domain.model.Action;

public interface ActionRequestContextFactory {

  ActionRequestContext getActionRequestDecoratorContext(Action action);
}
