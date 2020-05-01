package uk.gov.ons.ctp.response.action.service.decorator.context;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.SampleSvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleAttributesDTO;

@Component
@Qualifier("social")
public class SocialActionRequestContextFactory implements ActionRequestContextFactory {
  private static final Logger log =
      LoggerFactory.getLogger(SocialActionRequestContextFactory.class);

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
