package uk.gov.ons.ctp.response.action.service.impl.decorator;

import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.impl.decorator.context.ActionRequestContext;

public class SampleUnitRefAddress implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {

    final ActionAddress actionAddress = new ActionAddress();
    actionAddress.setSampleUnitRef(context.getCaseDetails().getCaseGroup().getSampleUnitRef());

    actionRequest.setAddress(actionAddress);
  }
}
