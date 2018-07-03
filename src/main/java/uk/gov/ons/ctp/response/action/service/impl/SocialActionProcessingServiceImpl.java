package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.SampleSvcClientService;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;

import java.util.UUID;

@Service
@Qualifier("social")
public class SocialActionProcessingServiceImpl extends ActionProcessingServiceImpl {

  @Autowired
  private ActionCaseRepository actionCaseRepo;

  @Autowired
  private SampleSvcClientService sampleSvcClient;

  @Override
  public ActionRequest prepareActionRequest(Action action) {

    SampleAttributesDTO sampleAttribs = getSampleAttributes(action);

    ActionRequest ar = new ActionRequest();
   // ar.set

    return new ActionRequest();
  }

  private SampleAttributesDTO getSampleAttributes(Action action) {

    UUID sampleUnitId = actionCaseRepo.findById(action.getCaseId()).getSampleUnitId();
    SampleAttributesDTO sampleAttribs = null;

    if (sampleUnitId != null) {
      sampleAttribs = sampleSvcClient.getSampleAttributes(sampleUnitId);
    }

    return sampleAttribs;
  }
}
