package uk.gov.ons.ctp.response.action.service.decorator;

import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;

@Slf4j
public class SampleAttributes implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    final ActionAddress actionAddress = new ActionAddress();
    Map<String, String> sampleAttribs = context.getSampleAttributes().getAttributes();
    log.debug("sampleAttributesDTO received: " + context.getSampleAttributes().toString());

    actionAddress.setLine1(Objects.toString(sampleAttribs.get("Prem1"), "Missing Address Line 1"));
    actionAddress.setLine2(sampleAttribs.get("Prem2"));
    actionAddress.setLine3(sampleAttribs.get("Prem3"));
    actionAddress.setLine4(sampleAttribs.get("Prem4"));
    actionAddress.setLocality(sampleAttribs.get("District"));
    actionAddress.setTownName(sampleAttribs.get("PostTown"));
    actionAddress.setPostcode(sampleAttribs.get("Postcode"));
    actionAddress.setSampleUnitRef(context.getCaseDetails().getCaseGroup().getSampleUnitRef());

    actionRequest.setAddress(actionAddress);
  }
}
