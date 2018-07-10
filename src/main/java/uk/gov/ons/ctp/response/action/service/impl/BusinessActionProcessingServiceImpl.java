package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.service.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionRequestDecoratorContextFactory;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CaseActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CollectionExerciseActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.PartyActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.SampleUnitRefAddressDecorator;

@Service
@Qualifier("business")
public class BusinessActionProcessingServiceImpl extends ActionProcessingServiceImpl {

  private static final ActionRequestDecorator[] BUSINESS_DECORATORS = {
    new ActionActionRequestDecorator(),
    new CaseActionRequestDecorator(),
    new CollectionExerciseActionRequestDecorator(),
    new PartyActionRequestDecorator(),
    new SampleUnitRefAddressDecorator()
  };

  @Autowired
  @Qualifier("business")
  private ActionRequestDecoratorContextFactory decoratorContextFactory;

  public BusinessActionProcessingServiceImpl() {
    super(BUSINESS_DECORATORS);
  }

  @Override
  public ActionRequestDecoratorContextFactory getActionRequestDecoratorContextFactory() {
    return decoratorContextFactory;
  }
}
