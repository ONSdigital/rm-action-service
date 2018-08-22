package uk.gov.ons.ctp.response.action.service.decorator;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;

public class CaseAndCaseEvent implements ActionRequestDecorator {
  private static final Logger log = LoggerFactory.getLogger(CaseAndCaseEvent.class);

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    actionRequest.setCaseId(context.getCaseDetails().getId().toString());
    actionRequest.setCaseRef(context.getCaseDetails().getCaseRef());
    actionRequest.setIac(context.getCaseDetails().getIac());

    final ActionEvent actionEvent = new ActionEvent();
    List<CaseEventDTO> caseEventDTOs = context.getCaseDetails().getCaseEvents();

    caseEventDTOs.forEach(
        (caseEventDTO) -> actionEvent.getEvents().add(formatCaseEvent(caseEventDTO)));
    actionRequest.setEvents(actionEvent);

    actionRequest.setCaseGroupStatus(
        context.getCaseDetails().getCaseGroup().getCaseGroupStatus().toString());
  }

  /**
   * Formats a CaseEvent as a string that can added to the ActionRequest
   *
   * @param caseEventDTO the DTO to be formatted
   * @return the pretty one liner
   */
  protected String formatCaseEvent(final CaseEventDTO caseEventDTO) {
    return String.format(
        "%s : %s : %s : %s",
        caseEventDTO.getCategory(),
        caseEventDTO.getSubCategory(),
        caseEventDTO.getCreatedBy(),
        caseEventDTO.getDescription());
  }
}
