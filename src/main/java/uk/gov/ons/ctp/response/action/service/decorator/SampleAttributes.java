package uk.gov.ons.ctp.response.action.service.decorator;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Map;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;

public class SampleAttributes implements ActionRequestDecorator {
  private static final Logger log = LoggerFactory.getLogger(SampleAttributes.class);

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    final ActionAddress actionAddress = new ActionAddress();
    Map<String, String> sampleAttribs = context.getSampleAttributes().getAttributes();
    log.debug("sampleAttributesDTO received: " + context.getSampleAttributes().toString());

    actionAddress.setLine1(sampleAttribs.get("ADDRESS_LINE1"));
    actionAddress.setLine2(sampleAttribs.get("ADDRESS_LINE2"));
    actionAddress.setLocality(sampleAttribs.get("LOCALITY"));
    actionAddress.setTownName(sampleAttribs.get("TOWN_NAME"));
    actionAddress.setPostcode(sampleAttribs.get("POSTCODE"));
    actionAddress.setCountry(sampleAttribs.get("COUNTRY"));
    actionAddress.setOrganisationName(sampleAttribs.get("ORGANISATION_NAME"));
    actionAddress.setSampleUnitRef(context.getCaseDetails().getCaseGroup().getSampleUnitRef());

    actionRequest.setAddress(actionAddress);
  }
}
