package uk.gov.ons.ctp.response.action.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.service.decorator.ActionAndActionPlan;
import uk.gov.ons.ctp.response.action.service.decorator.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.decorator.CaseAndCaseEvent;
import uk.gov.ons.ctp.response.action.service.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.decorator.PartyAndContact;
import uk.gov.ons.ctp.response.action.service.decorator.SampleUnitRefAddress;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;

@Service
@Qualifier("business")
public class BusinessActionProcessingService extends ActionProcessingService {

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

  public BusinessActionProcessingService() {
    super(BUSINESS_DECORATORS);
  }

  @Override
  public ActionRequestContextFactory getActionRequestDecoratorContextFactory() {
    return decoratorContextFactory;
  }
}
