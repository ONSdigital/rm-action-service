package uk.gov.ons.ctp.response.action.service.decorator.context;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.SampleSvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;

@Slf4j
@Component
@Qualifier("social")
public class SocialActionRequestContextFactory implements ActionRequestContextFactory {

  @Autowired private ActionCaseRepository actionCaseRepo;

  @Autowired private SampleSvcClientService sampleSvcClient;

  @Autowired private DefaultActionRequestContextFactory defaultFactory;

  @Override
  public ActionRequestContext getActionRequestDecoratorContext(Action action) {
    ActionRequestContext context = this.defaultFactory.getActionRequestDecoratorContext(action);

    UUID sampleUnitId = actionCaseRepo.findById(action.getCaseId()).getSampleUnitId();
    SampleAttributesDTO sampleAttribs = null;

    if (sampleUnitId != null) {
      sampleAttribs = sampleSvcClient.getSampleAttributes(sampleUnitId);

      context.setSampleAttributes(sampleAttribs);
    }

    return context;
  }
}
