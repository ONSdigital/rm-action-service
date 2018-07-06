package uk.gov.ons.ctp.response.action.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.Association;
import uk.gov.ons.ctp.response.party.representation.Attributes;
import uk.gov.ons.ctp.response.party.representation.Enrolment;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

@Slf4j
@Service
@Qualifier("business")
public class BusinessActionProcessingServiceImpl extends ActionProcessingServiceImpl {

  @Autowired private ActionPlanRepository actionPlanRepo;

  @Autowired private CaseSvcClientService caseSvcClientService;

  @Autowired private CollectionExerciseClientService collectionExerciseClientService;

  @Autowired private PartySvcClientService partySvcClientService;

  @Autowired private SurveySvcClientService surveySvcClientService;

  public static final String ENABLED = "ENABLED";
  public static final String PENDING = "PENDING";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";

  /**
   * Take an action and using it, fetch further info from Case service in a number of rest calls, in
   * order to create the ActionRequest
   *
   * @param action It all starts with the Action
   * @return The ActionRequest created from the Action and the other info from CaseSvc
   */
  public ActionRequest prepareActionRequest(final Action action) {
    final UUID caseId = action.getCaseId();
    log.debug(
        "constructing ActionRequest to publish to downstream handler for action {} and case id {}",
        action.getActionPK(),
        caseId);

    final ActionPlan actionPlan =
        (action.getActionPlanFK() == null)
            ? null
            : actionPlanRepo.findOne(action.getActionPlanFK());
    log.debug("actionPlan {}", actionPlan);

    final CaseDetailsDTO caseDTO = caseSvcClientService.getCaseWithIACandCaseEvents(caseId);
    final String sampleUnitTypeStr = caseDTO.getSampleUnitType();
    log.debug("sampleUnitTypeStr {}", sampleUnitTypeStr);
    if (validate(sampleUnitTypeStr)) {
      final SampleUnitDTO.SampleUnitType sampleUnitType =
          SampleUnitDTO.SampleUnitType.valueOf(sampleUnitTypeStr);
      log.debug("sampleUnitType is {}", sampleUnitType);

      final CaseGroupDTO caseGroupDTO = caseDTO.getCaseGroup();

      final UUID collectionExerciseId = caseGroupDTO.getCollectionExerciseId();
      final CollectionExerciseDTO collectionExercise =
          collectionExerciseClientService.getCollectionExercise(collectionExerciseId);

      final PartyDTO parentParty;
      PartyDTO childParty = null;
      if (sampleUnitType.isParent()) {
        parentParty =
            partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
                sampleUnitTypeStr, caseDTO.getPartyId(), collectionExercise.getSurveyId());
        log.debug("parentParty retrieved is {}", parentParty);
      } else {
        childParty = partySvcClientService.getParty(sampleUnitTypeStr, caseDTO.getPartyId());
        log.debug("childParty retrieved is {}", childParty);

        final UUID associatedParentPartyID = caseDTO.getCaseGroup().getPartyId();
        // For BRES, child sampleUnitTypeStr is BI. parent will thus be B.
        parentParty =
            partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
                sampleUnitTypeStr.substring(0, 1),
                associatedParentPartyID,
                collectionExercise.getSurveyId());
        log.debug("parentParty for the child retrieved is {}", parentParty);
      }

      final List<CaseEventDTO> caseEventDTOs = caseDTO.getCaseEvents();

      return populateActionRequest(
          action, actionPlan, caseDTO, parentParty, childParty, collectionExercise, caseEventDTOs);
    } else {
      return null;
    }
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

    if (enrolmentStatuses.contains(PENDING)) {
      enrolmentStatus = PENDING;
    }

    if (enrolmentStatuses.contains(ENABLED)) {
      enrolmentStatus = ENABLED;
    }

    return enrolmentStatus;
  }

  /**
   * Given the business objects passed, create, populate and return an ActionRequest
   *
   * @param action the persistent Action obj from the db
   * @param actionPlan the persistent ActionPlan obj from the db
   * @param caseDTO the Case representation from the CaseSvc
   * @param parentParty the parent Party from the PartySvc (in BRES, it is a B)
   * @param childParty the BI Party from the PartySvc (in BRES, it is a C)
   * @param caseEventDTOs the list of CaseEvent representations from the CaseSvc
   * @return the shiney new Action Request
   */
  private ActionRequest populateActionRequest(
      final Action action,
      final ActionPlan actionPlan,
      final CaseDetailsDTO caseDTO,
      final PartyDTO parentParty,
      final PartyDTO childParty,
      final CollectionExerciseDTO collectionExercise,
      final List<CaseEventDTO> caseEventDTOs) {

    // Everything from here ...
    final ActionRequest actionRequest = new ActionRequest();
    final String actionID = action.getId().toString();
    log.debug("actionID is {}", actionID);
    actionRequest.setActionId(actionID);
    actionRequest.setActionPlan((actionPlan == null) ? null : actionPlan.getName());
    actionRequest.setActionType(action.getActionType().getName());
    actionRequest.setResponseRequired(action.getActionType().getResponseRequired());
    actionRequest.setCaseId(action.getCaseId().toString());
    actionRequest.setCaseRef(caseDTO.getCaseRef());
    actionRequest.setExerciseRef(collectionExercise.getExerciseRef());

    final ActionEvent actionEvent = new ActionEvent();
    caseEventDTOs.forEach(
        (caseEventDTO) -> actionEvent.getEvents().add(formatCaseEvent(caseEventDTO)));
    actionRequest.setEvents(actionEvent);

    actionRequest.setIac(caseDTO.getIac());
    actionRequest.setPriority(
        Priority.fromValue(Action.ActionPriority.valueOf(action.getPriority()).getName()));

    final ActionAddress actionAddress = new ActionAddress();
    actionAddress.setSampleUnitRef(caseDTO.getCaseGroup().getSampleUnitRef());
    actionRequest.setAddress(actionAddress);

    final SurveyDTO surveyDTO =
        surveySvcClientService.requestDetailsForSurvey(collectionExercise.getSurveyId());
    actionRequest.setSurveyName(surveyDTO.getLongName());
    actionRequest.setSurveyRef(surveyDTO.getSurveyRef());
    actionRequest.setUserDescription(collectionExercise.getUserDescription());

    actionRequest.setLegalBasis(surveyDTO.getLegalBasis());
    // ... to here is common to both social and business

    final Attributes businessUnitAttributes = parentParty.getAttributes();
    actionRequest.setRegion(businessUnitAttributes.getRegion());

    final ActionContact actionContact = new ActionContact();
    actionContact.setRuName(businessUnitAttributes.getName());
    actionContact.setTradingStyle(generateTradingStyle(businessUnitAttributes));

    if (childParty != null) {
      // BI case
      actionRequest.setRespondentStatus(childParty.getStatus());

      final Attributes biPartyAttributes = childParty.getAttributes();
      populateContactDetails(biPartyAttributes, actionContact);
    } else {
      // B case
      final String parentUnitType = caseDTO.getSampleUnitType();

      // Map of child parties, key'd by their statuses
      final Map<String, PartyDTO> childPartyMapByStatus =
          getChildParties(parentParty, parentUnitType);

      actionRequest.setRespondentStatus(parseRespondentStatuses(childPartyMapByStatus));

      // B case with BI registered without a fully validated email
      // It needs the contact details of the non validated respondent sent to the business in the
      // print file
      if (CREATED.equals(actionRequest.getRespondentStatus())) {
        actionRequest.setIac(""); // Don't want to send this to the business, breaks if null

        final PartyDTO createdStatusChildParty = childPartyMapByStatus.get(CREATED);

        final Attributes childAttributes = createdStatusChildParty.getAttributes();
        populateContactDetails(childAttributes, actionContact);
      }
    }

    actionRequest.setContact(actionContact);

    actionRequest.setEnrolmentStatus(getEnrolmentStatus(parentParty));
    actionRequest.setCaseGroupStatus(caseDTO.getCaseGroup().getCaseGroupStatus().toString());

    final Date scheduledReturnDateTime = collectionExercise.getScheduledReturnDateTime();
    if (scheduledReturnDateTime != null) {
      final DateFormat df = new SimpleDateFormat(DATE_FORMAT_IN_REMINDER_EMAIL);
      actionRequest.setReturnByDate(df.format(scheduledReturnDateTime));
    }

    return actionRequest;
  }

  public void populateContactDetails(
      final Attributes attributes, final ActionContact actionContact) {
    actionContact.setForename(attributes.getFirstName());
    actionContact.setSurname(attributes.getLastName());
    actionContact.setEmailAddress(attributes.getEmailAddress());
  }

  /**
   * iterate through map of <RespondentStatus, ChildParties> and parse respondent statuses'
   *
   * @param childPartyMap
   * @return respondentStatus
   */
  public String parseRespondentStatuses(final Map<String, PartyDTO> childPartyMap) {

    String respondentStatus = null;

    if (childPartyMap.keySet().contains(CREATED)) {
      respondentStatus = CREATED;
    }

    if (childPartyMap.keySet().contains(ACTIVE)) {
      respondentStatus = ACTIVE;
    }

    return respondentStatus;
  }

  public Map<String, PartyDTO> getChildParties(
      final PartyDTO parentParty, final String parentUnitTypeStr) {
    final Map<String, PartyDTO> statusMapOfChildParties = new HashMap<>();

    final List<String> childPartyIds =
        parentParty
            .getAssociations()
            .stream()
            .map(Association::getPartyId)
            .collect(Collectors.toList());

    // ALl child parties are parent+I, i.e B & BI.
    final String childUnitTypeStr = parentUnitTypeStr + "I";

    for (final String id : childPartyIds) {
      final PartyDTO childParty = partySvcClientService.getParty(childUnitTypeStr, id);
      if (childParty != null) {
        statusMapOfChildParties.put(childParty.getStatus(), childParty);
      } else {
        log.info("Unable to get party with id, {}", id);
      }
    }
    return statusMapOfChildParties;
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
}
