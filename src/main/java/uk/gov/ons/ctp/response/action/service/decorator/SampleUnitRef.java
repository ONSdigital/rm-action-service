package uk.gov.ons.ctp.response.action.service.decorator;

import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;

public class SampleUnitRef implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    actionRequest.setSampleUnitRef(context.getCaseDetails().getCaseGroup().getSampleUnitRef());
  }
}
