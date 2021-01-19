package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionEvent;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.action.representation.events.ActionCaseParty;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class processEmailCases {
  private static final Logger log = LoggerFactory.getLogger(processEmailCases.class);
  private final ProcessEventService processEventService;
  private final NotifyEmailService emailService;

  public processEmailCases(ProcessEventService processEventService, NotifyEmailService emailService) {
    this.processEventService = processEventService;
    this.emailService = emailService;
  }

  public void processEmailCases(
    Instant instant,
    CollectionExerciseDTO collectionExercise,
    SurveyDTO survey,
    List<ActionCase> email_cases,
    ActionTemplate actionTemplate) {
    email_cases
      .parallelStream()
      .filter(c -> processEventService.isActionable(c, actionTemplate))
      .forEach(
        c -> processEmailCase(c, collectionExercise, survey, actionTemplate, null, instant));
  }

  private void processEmailCase(
    ActionCase actionCase,
    CollectionExerciseDTO collectionExercise,
    SurveyDTO survey,
    ActionTemplate actionTemplate,
    ActionEvent actionEvent,
    Instant instant) {
    UUID actionCaseId = actionCase.getId();
    String templateType = actionTemplate.getType();
    ActionTemplateDTO.Handler templateHandler = actionTemplate.getHandler();
    log.with("caseId", actionCaseId)
      .with("actionTemplate", templateType)
      .with("actionHandler", templateHandler)
      .info("Processing Email Event.");
    boolean isSuccess = true;
    try {
      log.with("caseId", actionCaseId).info("Getting ActionCaseParty");
      ActionCaseParty actionCaseParty = processEventService.setParties(actionCase, survey);
      if (processEventService.isBusinessNotification(actionCase)) {
        log.with("caseId", actionCase).info("Processing Email for isBusinessNotification true");
        actionCaseParty
          .getChildParties()
          .parallelStream()
          // .filter(respondentParty -> isActive(respondentParty))
          .forEach(
            respondentParty ->
              processEmail(
                actionCaseParty.getParentParty(),
                respondentParty,
                survey,
                actionTemplate,
                actionCase,
                collectionExercise));
      } else {
        log.with("caseId", actionCase).info("Processing Email for isBusinessNotification false");
        processEmail(
          actionCaseParty.getParentParty(),
          actionCaseParty.getChildParties().get(0),
          survey,
          actionTemplate,
          actionCase,
          collectionExercise);
      }
    } catch (Exception e) {
      log.with("caseId", actionCaseId)
        .with("actionTemplate", templateType)
        .with("actionHandler", templateHandler)
        .warn("Processing Email Event FAILED.");
      isSuccess = false;
    }
    if (actionEvent == null) {
      processEventService.createCaseActionEvent(
        actionCaseId,
        templateType,
        templateHandler,
        isSuccess,
        collectionExercise.getId(),
        survey.getId(),
        instant);
    } else {
      processEventService.updateCaseActionEvent(actionEvent, instant);
    }
  }

  private void processEmail(
    PartyDTO businessParty,
    PartyDTO respondentParty,
    SurveyDTO survey,
    ActionTemplate actionTemplate,
    ActionCase actionCase,
    CollectionExerciseDTO collectionExercise) {
    log.with("template", actionTemplate.getType())
      .with("case", actionCase.getId())
      .with("handler", actionTemplate.getHandler())
      .info("Collecting email data.");
    String sampleUnitRef = actionCase.getSampleUnitRef();
    // This is only to handle legacy cases
    if (sampleUnitRef == null) {
      CaseDetailsDTO caseDetailsDTO = processEventService.getCaseDetails(actionCase);
      sampleUnitRef = caseDetailsDTO.getCaseGroup().getSampleUnitRef();
    }
    NotifyModel.Notify.Classifiers classifiers = processEventService.getClassifiers(businessParty, survey, actionTemplate);
    NotifyModel.Notify.Personalisation personalisation =
      processEventService.getPersonalisation(
        businessParty, respondentParty, survey, sampleUnitRef, collectionExercise);
    NotifyModel payload =
      new NotifyModel(
        NotifyModel.Notify.builder()
          .personalisation(personalisation)
          .classifiers(classifiers)
          .emailAddress(respondentParty.getAttributes().getEmailAddress())
          .build());
    log.with("template", actionTemplate.getType())
      .with("case", actionCase.getId())
      .with("handler", actionTemplate.getHandler())
      .info("sending email data to pubsub.");
    emailService.processEmail(payload);
  }
}
