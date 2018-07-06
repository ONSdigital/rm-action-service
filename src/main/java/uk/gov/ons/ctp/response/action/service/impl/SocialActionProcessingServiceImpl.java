package uk.gov.ons.ctp.response.action.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.SampleSvcClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

@Slf4j
@Service
@Qualifier("social")
public class SocialActionProcessingServiceImpl extends ActionProcessingServiceImpl {

  @Autowired private ActionCaseRepository actionCaseRepo;

  @Autowired private SampleSvcClientService sampleSvcClient;

  @Autowired private ActionPlanRepository actionPlanRepo;

  @Autowired private CaseSvcClientService caseSvcClientService;

  @Autowired private CollectionExerciseClientService collectionExerciseClientService;

  @Autowired private SurveySvcClientService surveySvcClientService;

  @Override
  public ActionRequest prepareActionRequest(Action action) {
    ActionRequestHelper actionRequestHelper =
        new ActionRequestHelper(
            actionPlanRepo, caseSvcClientService, collectionExerciseClientService);

    actionRequestHelper.prepareActionRequest(action);

    final ActionRequest actionRequest = new ActionRequest();
    final String actionID = action.getId().toString();
    final ActionPlan actionPlan = actionRequestHelper.getActionPlan();
    log.debug("actionID is {}", actionID);
    actionRequest.setActionId(actionID);
    actionRequest.setActionPlan((actionPlan == null) ? null : actionPlan.getName());
    actionRequest.setActionType(action.getActionType().getName());
    actionRequest.setResponseRequired(action.getActionType().getResponseRequired());
    actionRequest.setCaseId(action.getCaseId().toString());

    CaseDetailsDTO caseDTO = actionRequestHelper.getCaseDTO();
    actionRequest.setCaseRef(caseDTO.getCaseRef());

    CollectionExerciseDTO collectionExercise = actionRequestHelper.getCollectionExerciseDTO();
    actionRequest.setExerciseRef(collectionExercise.getExerciseRef());

    final ActionEvent actionEvent = new ActionEvent();
    final List<CaseEventDTO> caseEventDTOs = actionRequestHelper.getCaseDTO().getCaseEvents();
    caseEventDTOs.forEach(
        (caseEventDTO) -> actionEvent.getEvents().add(formatCaseEvent(caseEventDTO)));
    actionRequest.setEvents(actionEvent);

    actionRequest.setIac(caseDTO.getIac());
    actionRequest.setPriority(
        Priority.fromValue(Action.ActionPriority.valueOf(action.getPriority()).getName()));

    // Here, we're setting the sample attributes for address
    // **************************************
    final ActionAddress actionAddress = new ActionAddress();
    actionAddress.setSampleUnitRef(caseDTO.getCaseGroup().getSampleUnitRef());

    final SampleAttributesDTO sampleAttributesDTO = getSampleAttributes(action);
    Map<String, String> sampleAttribs = sampleAttributesDTO.getAttributes();
    log.debug("sampleAttributesDTO received: " + sampleAttributesDTO.toString());

    actionAddress.setLine1(Objects.toString(sampleAttribs.get("Prem1"), "Missing Address Line 1"));
    actionAddress.setLine2(sampleAttribs.get("Prem2"));
    actionAddress.setLine3(sampleAttribs.get("Prem3"));
    actionAddress.setLine4(sampleAttribs.get("Prem4"));
    actionAddress.setLocality(sampleAttribs.get("District"));
    actionAddress.setTownName(sampleAttribs.get("PostTown"));
    actionAddress.setPostcode(sampleAttribs.get("Postcode"));
    actionAddress.setSampleUnitRef(caseDTO.getCaseGroup().getSampleUnitRef());

    actionRequest.setAddress(actionAddress);

    final SurveyDTO surveyDTO =
        surveySvcClientService.requestDetailsForSurvey(collectionExercise.getSurveyId());
    actionRequest.setSurveyName(surveyDTO.getLongName());
    actionRequest.setSurveyRef(surveyDTO.getSurveyRef());
    actionRequest.setUserDescription(collectionExercise.getUserDescription());

    actionRequest.setLegalBasis(surveyDTO.getLegalBasis());
    // ... to here is common to both social and business

    actionRequest.setRegion(sampleAttribs.get("District"));

    actionRequest.setCaseGroupStatus(caseDTO.getCaseGroup().getCaseGroupStatus().toString());

    final Date scheduledReturnDateTime = collectionExercise.getScheduledReturnDateTime();
    if (scheduledReturnDateTime != null) {
      final DateFormat df = new SimpleDateFormat(DATE_FORMAT_IN_REMINDER_EMAIL);
      actionRequest.setReturnByDate(df.format(scheduledReturnDateTime));
    }

    return actionRequest;
  }

  private SampleAttributesDTO getSampleAttributes(Action action) {

    UUID sampleUnitId = actionCaseRepo.findById(action.getCaseId()).getSampleUnitId();
    SampleAttributesDTO sampleAttribs = null;

    if (sampleUnitId != null) {
      sampleAttribs = sampleSvcClient.getSampleAttributes(sampleUnitId);
    }

    return sampleAttribs;
  }
}
