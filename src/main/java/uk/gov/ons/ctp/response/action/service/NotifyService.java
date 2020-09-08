package uk.gov.ons.ctp.response.action.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.PubsubConfig.PublisherSupplier;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

@Service
public class NotifyService {

  @Autowired private PublisherSupplier publisherSupplier;

  public void processNotification(ActionRequest actionRequest) {}
}
