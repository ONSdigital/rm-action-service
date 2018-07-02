package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

@Service
@Qualifier("social")
public class SocialActionProcessingServiceImpl extends ActionProcessingServiceImpl {
  @Override
  public ActionRequest prepareActionRequest(Action action) {
    return new ActionRequest();
  }
}
