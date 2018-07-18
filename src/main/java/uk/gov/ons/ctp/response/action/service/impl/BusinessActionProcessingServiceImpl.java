package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionAndActionPlan;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CaseAndCaseEvent;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.impl.decorator.PartyAndContact;
import uk.gov.ons.ctp.response.action.service.impl.decorator.SampleUnitRefAddress;
import uk.gov.ons.ctp.response.action.service.impl.decorator.context.ActionRequestContextFactory;

@Service
@Qualifier("business")
public class BusinessActionProcessingServiceImpl extends ActionProcessingServiceImpl {

  private static final ActionRequestDecorator[] BUSINESS_DECORATORS = {
    new ActionAndActionPlan(),
    new CaseAndCaseEvent(),
    new CollectionExerciseAndSurvey(),
    new PartyAndContact(),
    new SampleUnitRefAddress()
  };

  @Autowired
  @Qualifier("business")
  private ActionRequestContextFactory decoratorContextFactory;

  public BusinessActionProcessingServiceImpl() {
    super(BUSINESS_DECORATORS);
  }

  @Override
  public ActionRequestContextFactory getActionRequestDecoratorContextFactory() {
    return decoratorContextFactory;
  }
}
