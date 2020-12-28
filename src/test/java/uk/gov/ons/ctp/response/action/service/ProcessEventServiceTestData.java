package uk.gov.ons.ctp.response.action.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionEvent;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO.Handler;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.Association;
import uk.gov.ons.ctp.response.lib.party.representation.Attributes;
import uk.gov.ons.ctp.response.lib.party.representation.Enrolment;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

public class ProcessEventServiceTestData {

  public CollectionExerciseDTO setupCollectionExerciseDTO(
      UUID collectionExerciseId, UUID surveyId, String surveyRef, String description) {
    CollectionExerciseDTO collectionExerciseDTO = new CollectionExerciseDTO();
    collectionExerciseDTO.setId(collectionExerciseId);
    collectionExerciseDTO.setSurveyId(surveyId.toString());
    collectionExerciseDTO.setSurveyRef(surveyRef);
    collectionExerciseDTO.setUserDescription(description);
    collectionExerciseDTO.setScheduledReturnDateTime(new Date(29012021));
    collectionExerciseDTO.setUserDescription("Test");
    return collectionExerciseDTO;
  }

  public SurveyDTO setupSurveyDTO(
      UUID surveyId, String leagalBais, String surveyRef, String longName) {
    SurveyDTO survey = new SurveyDTO();
    survey.setId(surveyId.toString());
    survey.setLegalBasis(leagalBais);
    survey.setLongName(longName);
    survey.setSurveyRef(surveyRef);
    return survey;
  }

  public ActionTemplate setupActionTemplate(String type, Handler handler, String tag) {
    return ActionTemplate.builder()
        .type(type)
        .description(type + handler.toString() + tag)
        .handler(handler)
        .tag(tag)
        .build();
  }

  public ActionCase setupActionCase(
      UUID caseId,
      boolean activeEnrolment,
      UUID collectionExerciseId,
      UUID partyId,
      String sampleUnitType,
      UUID sampleUnitId,
      String sampleUnitRef,
      String iac) {
    return ActionCase.builder()
        .id(caseId)
        .activeEnrolment(activeEnrolment)
        .collectionExerciseId(collectionExerciseId)
        .partyId(partyId)
        .sampleUnitId(sampleUnitId)
        .sampleUnitRef(sampleUnitRef)
        .sampleUnitType(sampleUnitType)
        .iac(iac)
        .build();
  }

  public PartyDTO setupBusinessParty(
      String id, String region, String name, String email, String resPartyId) {
    PartyDTO businessParty = new PartyDTO();
    Attributes attributes = new Attributes();
    Enrolment enrolment = new Enrolment();
    enrolment.setEnrolmentStatus("ACTIVE");
    attributes.setRegion(region);
    attributes.setName(name);
    attributes.setEmailAddress(email);
    List<Association> associationList = new ArrayList<>();
    List<Enrolment> enrolments = new ArrayList<>();
    enrolments.add(enrolment);
    Association association = new Association();
    association.setPartyId(resPartyId);
    association.setEnrolments(enrolments);
    associationList.add(association);
    businessParty.setId(id);
    businessParty.setName(name);
    businessParty.setAttributes(attributes);
    businessParty.setAssociations(associationList);
    businessParty.setStatus("ACTIVE");
    return businessParty;
  }

  public PartyDTO setupRespondentParty(String name, String lastName, String email, String partyId) {
    PartyDTO respondentParty = new PartyDTO();
    Attributes attributes = new Attributes();
    attributes.setFirstName(name);
    attributes.setLastName(lastName);
    attributes.setEmailAddress(email);
    respondentParty.setId(partyId);
    respondentParty.setAttributes(attributes);
    respondentParty.setStatus("ACTIVE");
    return respondentParty;
  }

  public ActionEvent setActionEvent(
      UUID caseid,
      UUID surveyId,
      UUID collectionExerciseId,
      ActionEvent.ActionEventStatus status,
      Handler handler,
      String type) {
    return ActionEvent.builder()
        .caseId(caseid)
        .surveyId(surveyId)
        .collectionExerciseId(collectionExerciseId)
        .status(status)
        .handler(handler)
        .type(type)
        .build();
  }
}
