package uk.gov.ons.ctp.response.action.service.impl.decorator;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.service.SampleSvcClientService;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;

@Slf4j
@Component
@Qualifier("social")
public class SocialActionRequestDecoratorContextFactoryImpl
    extends ActionRequestDecoratorContextFactoryImpl {

  @Autowired private ActionCaseRepository actionCaseRepo;

  @Autowired private SampleSvcClientService sampleSvcClient;

  @Override
  public ActionRequestDecoratorContext getActionRequestDecoratorContext(Action action) {
    ActionRequestDecoratorContext context = super.getActionRequestDecoratorContext(action);

    UUID sampleUnitId = actionCaseRepo.findById(action.getCaseId()).getSampleUnitId();
    SampleAttributesDTO sampleAttribs = null;

    if (sampleUnitId != null) {
      sampleAttribs = sampleSvcClient.getSampleAttributes(sampleUnitId);

      context.setSampleAttributes(sampleAttribs);
    }

    return context;
  }
}
