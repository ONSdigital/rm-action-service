package uk.gov.ons.ctp.response.action.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.service.decorator.ActionAndActionPlan;
import uk.gov.ons.ctp.response.action.service.decorator.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.decorator.CaseAndCaseEvent;
import uk.gov.ons.ctp.response.action.service.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.decorator.SampleAttributes;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;

@Service
@Qualifier("social")
public class SocialActionProcessingServiceImpl extends ActionProcessingService {

  @Autowired
  @Qualifier("social")
  private ActionRequestContextFactory decoratorContextFactory;

  private static final ActionRequestDecorator[] SOCIAL_DECORATORS = {
    new ActionAndActionPlan(),
    new CaseAndCaseEvent(),
    new CollectionExerciseAndSurvey(),
    new SampleAttributes()
  };

  public SocialActionProcessingServiceImpl() {
    super(SOCIAL_DECORATORS);
  }

  @Override
  public ActionRequestContextFactory getActionRequestDecoratorContextFactory() {
    return decoratorContextFactory;
  }
}
