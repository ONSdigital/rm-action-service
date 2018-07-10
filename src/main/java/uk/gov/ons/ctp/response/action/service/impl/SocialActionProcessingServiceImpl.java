package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.service.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.ActionRequestDecoratorContextFactory;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CaseActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.CollectionExerciseActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.impl.decorator.SampleActionRequestDecorator;

@Service
@Qualifier("social")
public class SocialActionProcessingServiceImpl extends ActionProcessingServiceImpl {

  @Autowired
  @Qualifier("social")
  private ActionRequestDecoratorContextFactory decoratorContextFactory;

  private static final ActionRequestDecorator[] SOCIAL_DECORATORS = {
    new ActionActionRequestDecorator(),
    new CaseActionRequestDecorator(),
    new CollectionExerciseActionRequestDecorator(),
    new SampleActionRequestDecorator()
  };

  public SocialActionProcessingServiceImpl() {
    super(SOCIAL_DECORATORS);
  }

  @Override
  public ActionRequestDecoratorContextFactory getActionRequestDecoratorContextFactory() {
    return decoratorContextFactory;
  }
}
