package uk.gov.ons.ctp.response.action.service.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.party.representation.Association;
import uk.gov.ons.ctp.response.party.representation.Attributes;
import uk.gov.ons.ctp.response.party.representation.Enrolment;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;

@Slf4j
public class PartyAndContact implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {
    final PartyDTO parentParty = context.getParentParty();
    final List<PartyDTO> childParties = context.getChildParties();
    final Attributes businessUnitAttributes = parentParty.getAttributes();
    actionRequest.setRegion(businessUnitAttributes.getRegion());

    final ActionContact actionContact = new ActionContact();
    actionContact.setRuName(businessUnitAttributes.getName());
    actionContact.setTradingStyle(generateTradingStyle(businessUnitAttributes));

    if (context.getSampleUnitType().isParent() == false) {
      // BI case - this means the action request is being raised for the child party so
      // their details are the only ones we're interested in (and we can assume the first and
      // only element in the childParties list contains the details we are after)
      PartyDTO childParty = childParties.get(0);
      actionRequest.setRespondentStatus(childParty.getStatus());

      final Attributes biPartyAttributes = childParty.getAttributes();
      populateContactDetails(biPartyAttributes, actionContact);
    } else {
      // B case
      actionRequest.setRespondentStatus(parseRespondentStatuses(context.getChildParties()));

      // B case with BI registered without a fully validated email
      // It needs the contact details of the non validated respondent sent to the business in the
      // print file
      List<PartyDTO> createdChildParties =
          filterListByStatus(context.getChildParties(), ActionProcessingService.CREATED);

      if (createdChildParties != null && createdChildParties.size() > 0) {
        actionRequest.setIac(""); // Don't want to send this to the business, breaks if null

        final PartyDTO createdStatusChildParty = createdChildParties.get(0);

        final Attributes childAttributes = createdStatusChildParty.getAttributes();
        populateContactDetails(childAttributes, actionContact);
      }

      // NOTE: if there is a child party with status ACTIVE nothing gets added to the action request
    }

    actionRequest.setContact(actionContact);
    actionRequest.setEnrolmentStatus(getEnrolmentStatus(parentParty));
  }

  /**
   * Concatenate the businessUnitAttributes trading style fields into a single string
   *
   * @param businessUnitAttributes
   * @return concatenated trading styles
   */
  public String generateTradingStyle(final Attributes businessUnitAttributes) {
    final List<String> tradeStyles =
        Arrays.asList(
            businessUnitAttributes.getTradstyle1(),
            businessUnitAttributes.getTradstyle2(),
            businessUnitAttributes.getTradstyle3());
    return tradeStyles.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
  }

  public void populateContactDetails(
      final Attributes attributes, final ActionContact actionContact) {
    actionContact.setForename(attributes.getFirstName());
    actionContact.setSurname(attributes.getLastName());
    actionContact.setEmailAddress(attributes.getEmailAddress());
  }

  /**
   * iterate through map of <RespondentStatus, ChildParties> and parse respondent statuses. If
   * there's an ACTIVE party then the collective respondent status is ACTIVE. If there's a CREATED
   * party then the collective respondent status is CREATED else the respondent status is null
   *
   * @param childParties
   * @return respondentStatus
   */
  public String parseRespondentStatuses(final List<PartyDTO> childParties) {

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

  public List<PartyDTO> filterListByStatus(List<PartyDTO> parties, String status) {
    return parties == null
        ? null
        : parties.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
  }

  /**
   * enrolment status for the case based off the enrolled parties
   *
   * @param parentParty
   * @return enrolment status
   */
  public String getEnrolmentStatus(final PartyDTO parentParty) {
    final List<String> enrolmentStatuses = new ArrayList<>();

    final List<Association> associations = parentParty.getAssociations();
    if (associations != null) {
      for (final Association association : associations) {
        for (final Enrolment enrolment : association.getEnrolments()) {
          enrolmentStatuses.add(enrolment.getEnrolmentStatus());
        }
      }
    }

    String enrolmentStatus = null;

    if (enrolmentStatuses.contains(ActionProcessingService.PENDING)) {
      enrolmentStatus = ActionProcessingService.PENDING;
    }

    if (enrolmentStatuses.contains(ActionProcessingService.ENABLED)) {
      enrolmentStatus = ActionProcessingService.ENABLED;
    }

    return enrolmentStatus;
  }
}
