package uk.gov.ons.ctp.response.action.service.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

import java.util.List;
import java.util.UUID;

@Data
@Slf4j
public class ActionRequestHelper {

  @Getter(AccessLevel.NONE)
  private ActionPlanRepository actionPlanRepo;

  @Getter(AccessLevel.NONE)
  private CaseSvcClientService caseSvcClientService;

  @Getter(AccessLevel.NONE)
  private CollectionExerciseClientService collectionExerciseClientService;

  private CaseDetailsDTO caseDTO;
  private CaseGroupDTO caseGroupDTO;
  private CollectionExerciseDTO collectionExerciseDTO;
  private ActionPlan actionPlan;
  private List<CaseEventDTO> caseEventDTOs;

  public ActionRequestHelper(ActionPlanRepository actionPlanRepo, CaseSvcClientService caseSvcClientService,
          CollectionExerciseClientService collectionExerciseClientService) {
    this.actionPlanRepo = actionPlanRepo;
    this.caseSvcClientService = caseSvcClientService;
    this.collectionExerciseClientService = collectionExerciseClientService;
  }

  public void prepareActionRequest(Action action) {
    final UUID caseId = action.getCaseId();
    log.debug(
        "constructing ActionRequest to publish to downstream handler for action {} and case id {}",
        action.getActionPK(),
        caseId);

    actionPlan =
        (action.getActionPlanFK() == null)
            ? null
            : actionPlanRepo.findOne(action.getActionPlanFK());
    log.debug("actionPlan {}", actionPlan);

    caseDTO = caseSvcClientService.getCaseWithIACandCaseEvents(caseId);
    final String sampleUnitTypeStr = caseDTO.getSampleUnitType();
    log.debug("sampleUnitTypeStr {}", sampleUnitTypeStr);
    if (validate(sampleUnitTypeStr)) {
      final SampleUnitDTO.SampleUnitType sampleUnitType =
          SampleUnitDTO.SampleUnitType.valueOf(sampleUnitTypeStr);
      log.debug("sampleUnitType is {}", sampleUnitType);

      caseGroupDTO = caseDTO.getCaseGroup();

      final UUID collectionExerciseId = caseGroupDTO.getCollectionExerciseId();
      collectionExerciseDTO =
          collectionExerciseClientService.getCollectionExercise(collectionExerciseId);
    }
  }

  public ActionRequest populateActionRequest(Action action) {
    final ActionRequest actionRequest = new ActionRequest();
    final String actionID = action.getId().toString();

    log.debug("actionID is {}", actionID);
    actionRequest.setActionId(actionID);
    actionRequest.setActionPlan((actionPlan == null) ? null : actionPlan.getName());
    actionRequest.setActionType(action.getActionType().getName());
    actionRequest.setResponseRequired(action.getActionType().getResponseRequired());
    actionRequest.setCaseId(action.getCaseId().toString());
    actionRequest.setCaseRef(caseDTO.getCaseRef());
    actionRequest.setExerciseRef(collectionExerciseDTO.getExerciseRef());

    final ActionEvent actionEvent = new ActionEvent();
    caseEventDTOs = caseDTO.getCaseEvents();

    caseEventDTOs.forEach(
            (caseEventDTO) -> actionEvent.getEvents().add(formatCaseEvent(caseEventDTO)));
    actionRequest.setEvents(actionEvent);

    return actionRequest;
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

  /**
   * To validate the sampleUnitTypeStr versus SampleSvc-Api
   *
   * @param sampleUnitTypeStr the string value for sampleUnitType
   * @return true if sampleUnitTypeStr is known to us
   */
  public boolean validate(final String sampleUnitTypeStr) {
    boolean result = false;
    try {
      final SampleUnitDTO.SampleUnitType sampleUnitType =
          SampleUnitDTO.SampleUnitType.valueOf(sampleUnitTypeStr);
      log.debug("sampleUnitType {}", sampleUnitType);
      if (sampleUnitType.isParent()) {
        result = true;
      } else {
        final String childSampleUnitTypeStr = sampleUnitTypeStr.substring(0, 1);
        SampleUnitDTO.SampleUnitType.valueOf(childSampleUnitTypeStr);
        result = true;
      }
    } catch (final IllegalArgumentException e) {
      log.error(
          "Unexpected scenario. Error message is {}. Cause is {}", e.getMessage(), e.getCause());
      log.error("Stacktrace: ", e);
    }

    return result;
  }
}
