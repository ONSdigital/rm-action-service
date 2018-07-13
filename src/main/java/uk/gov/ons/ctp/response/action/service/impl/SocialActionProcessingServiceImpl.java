package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionAndActionPlan;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CaseAndCaseEvent;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.impl.decorator.SampleAttributes;
import uk.gov.ons.ctp.response.action.service.impl.decorator.context.ActionRequestContextFactory;

@Service
@Qualifier("social")
public class SocialActionProcessingServiceImpl extends ActionProcessingServiceImpl {

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
