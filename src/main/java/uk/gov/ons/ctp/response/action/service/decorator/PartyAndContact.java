package uk.gov.ons.ctp.response.action.service.decorator;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.lib.party.representation.Association;
import uk.gov.ons.ctp.response.lib.party.representation.Attributes;
import uk.gov.ons.ctp.response.lib.party.representation.Enrolment;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;

public class PartyAndContact implements ActionRequestDecorator {
  private static final Logger log = LoggerFactory.getLogger(PartyAndContact.class);
  private static final String NOTIFY = "Notify";
  private static final String PRINTER = "Printer";
  private static final String INVALID_SAMPLE_UNIT_TYPE_AND_HANDLER =
      "Invalid sample unit type and handler combination";
  private static final String WRONG_NUMBER_OF_RESPONDENT_PARTIES =
      "There should only one respondent party per notify request, instead there are %s";

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    final PartyDTO businessParty = context.getParentParty();
    final List<PartyDTO> respondentParties = context.getChildParties();

    final Attributes businessUnitAttributes = businessParty.getAttributes();
    actionRequest.setRegion(businessUnitAttributes.getRegion());
    actionRequest.setEnrolmentStatus(getEnrolmentStatus(businessParty));

    final ActionContact actionContact = new ActionContact();
    actionContact.setRuName(businessUnitAttributes.getName());
    actionContact.setTradingStyle(generateTradingStyle(businessUnitAttributes));
    if (isNotifyType(context)) {
      decorateNotifyType(respondentParties, actionRequest, actionContact);
    } else if (isPrinterType(context)) {
      decoratePrinterType(respondentParties, actionRequest, actionContact);
    } else {
      throw new IllegalStateException(INVALID_SAMPLE_UNIT_TYPE_AND_HANDLER);
    }
    actionRequest.setContact(actionContact);
  }

  private String getEnrolmentStatus(final PartyDTO parentParty) {
    final List<String> enrolmentStatuses = new ArrayList<>();

    final List<Association> associations = parentParty.getAssociations();
    if (associations != null) {
      for (Association association : associations) {
        for (Enrolment enrolment : association.getEnrolments()) {
          enrolmentStatuses.add(enrolment.getEnrolmentStatus());
        }
      }
    }

    String enrolmentStatus = null;
    if (enrolmentStatuses.contains(ActionProcessingService.ENABLED)) {
      enrolmentStatus = ActionProcessingService.ENABLED;
    } else if (enrolmentStatuses.contains(ActionProcessingService.PENDING)) {
      enrolmentStatus = ActionProcessingService.PENDING;
    }
    return enrolmentStatus;
  }

  private String generateTradingStyle(final Attributes businessUnitAttributes) {
    final List<String> tradeStyles =
        Arrays.asList(
            businessUnitAttributes.getTradstyle1(),
            businessUnitAttributes.getTradstyle2(),
            businessUnitAttributes.getTradstyle3());
    return tradeStyles.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
  }

  private boolean isNotifyType(ActionRequestContext context) {
    return context.getAction().getActionType().getHandler().equals(NOTIFY);
  }

  private void decorateNotifyType(
      List<PartyDTO> respondentParties, ActionRequest actionRequest, ActionContact actionContact) {
    // We should only have one respondent party per notify request now
    if (respondentParties.size() != 1) {
      throw new IllegalStateException(
          String.format(WRONG_NUMBER_OF_RESPONDENT_PARTIES, respondentParties.size()));
    }
    PartyDTO respondentParty = respondentParties.get(0);
    actionRequest.setRespondentStatus(respondentParty.getStatus());
    populateContactDetails(respondentParty.getAttributes(), actionContact);
  }

  private boolean isPrinterType(ActionRequestContext context) {
    return context.getAction().getActionType().getHandler().equals(PRINTER);
  }

  private void decoratePrinterType(
      List<PartyDTO> respondentParties, ActionRequest actionRequest, ActionContact actionContact) {
    actionRequest.setRespondentStatus(parseRespondentStatuses(respondentParties));
    List<PartyDTO> createdRespondentParties =
        filterListByStatus(respondentParties, ActionProcessingService.CREATED);
    if (createdRespondentParties != null && createdRespondentParties.size() > 0) {
      actionRequest.setIac(""); // Don't want to send this to the business, breaks if null
      populateContactDetails(createdRespondentParties.get(0).getAttributes(), actionContact);
    }
  }

  private void populateContactDetails(
      final Attributes attributes, final ActionContact actionContact) {
    actionContact.setForename(attributes.getFirstName());
    actionContact.setSurname(attributes.getLastName());
    actionContact.setEmailAddress(attributes.getEmailAddress());
  }

  private List<PartyDTO> filterListByStatus(List<PartyDTO> parties, String status) {
    return parties == null
        ? null
        : parties.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
  }

  private String parseRespondentStatuses(final List<PartyDTO> childParties) {

    String respondentStatus = null;

    if (childParties != null) {
      List<PartyDTO> activeParties =
          filterListByStatus(childParties, ActionProcessingService.ACTIVE);

      if (activeParties.size() > 0) {
        respondentStatus = ActionProcessingService.ACTIVE;
      } else {
        List<PartyDTO> createdParties =
            filterListByStatus(childParties, ActionProcessingService.CREATED);

        if (createdParties.size() > 0) {
          respondentStatus = ActionProcessingService.CREATED;
        }
      }
    }

    return respondentStatus;
  }
}
