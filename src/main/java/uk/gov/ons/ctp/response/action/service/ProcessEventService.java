package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.client.SurveySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionEvent;
import uk.gov.ons.ctp.response.action.domain.model.ActionEventPartialEntry;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionEventPartialEntryRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionEventRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.action.printfile.Contact;
import uk.gov.ons.ctp.response.action.printfile.LetterEntry;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.action.representation.events.ActionCaseParty;
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify.Classifiers;
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify.Personalisation;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.Association;
import uk.gov.ons.ctp.response.lib.party.representation.Attributes;
import uk.gov.ons.ctp.response.lib.party.representation.Enrolment;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@Service
public class ProcessEventService {
  private static final Logger log = LoggerFactory.getLogger(ProcessEventService.class);

  private final ActionTemplateService actionTemplateService;
  private final ActionCaseRepository actionCaseRepository;
  private final ActionEventRepository actionEventRepository;
  private final CollectionExerciseClientService collectionExerciseClientService;
  private final SurveySvcClientService surveySvcClientService;
  private final PartySvcClientService partySvcClientService;
  private final NotifyLetterService printFileService;
  private final NotifyEmailService emailService;
  private final ActionTemplateRepository actionTemplateRepository;
  private final CaseSvcClientService caseSvcClientService;
  private final ActionEventPartialEntryRepository actionEventPartialEntryRepository;

  public ProcessEventService(
      ActionCaseRepository actionCaseRepository,
      ActionTemplateService actionTemplateService,
      CollectionExerciseClientService collectionExerciseClientService,
      SurveySvcClientService surveySvcClientService,
      PartySvcClientService partySvcClientService,
      ActionEventRepository actionEventRepository,
      NotifyLetterService printFileService,
      NotifyEmailService emailService,
      ActionTemplateRepository actionTemplateRepository,
      CaseSvcClientService caseSvcClientService,
      ActionEventPartialEntryRepository actionEventPartialEntryRepository) {
    this.actionCaseRepository = actionCaseRepository;
    this.actionTemplateService = actionTemplateService;
    this.collectionExerciseClientService = collectionExerciseClientService;
    this.surveySvcClientService = surveySvcClientService;
    this.partySvcClientService = partySvcClientService;
    this.actionEventRepository = actionEventRepository;
    this.printFileService = printFileService;
    this.emailService = emailService;
    this.actionTemplateRepository = actionTemplateRepository;
    this.caseSvcClientService = caseSvcClientService;
    this.actionEventPartialEntryRepository = actionEventPartialEntryRepository;
  }

  /**
   * Processes Events. This method takes two attributes collection Exercise Id and Event Tag.
   * GetsCollectionExerciseDTO and SurveyDTO from @param collectionExerciseId Gets all email cases
   * against collectionExerciseID (isActiveEnrolment == true) Maps email cases to ActionTemplate
   * processes email cases if actionable Gets all letter cases against collectionExerciseID
   * (isActiveEnrolment == false) Maps letter cases to ActionTemplate processes letter cases if
   * actionable
   *
   * @param collectionExerciseId - is passed by collectionexercisesvc scheduler as a part of the
   *     event process.
   * @param eventTag - is passed by collectionexercisesvc scheduler as a part of the event process.
   */
  @Async
  public void processEvents(
      UUID collectionExerciseId, String eventTag, Optional<ActionEventPartialEntry> partialEntry) {
    log.with("collectionExerciseId", collectionExerciseId)
        .with("event_tag", eventTag)
        .info("Started processing");
    Instant instant = Instant.now();
    CollectionExerciseDTO collectionExercise = getCollectionExercise(collectionExerciseId);
    SurveyDTO survey = getSurvey(collectionExercise.getSurveyId());
    partialProcessCheckPoint(collectionExerciseId, eventTag, partialEntry, instant);
    log.debug("Getting Email cases against collectionExerciseId and event active enrolment");
    List<ActionCase> emailCases =
        actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, true);
    if (!emailCases.isEmpty()) {
      log.with("collectionExerciseId", collectionExercise).info("Processing email cases.");
      ActionTemplate actionTemplate = actionTemplateService.mapEventTagToTemplate(eventTag, true);
      if (actionTemplate != null) {
        log.with("email cases", emailCases.size()).info("Processing email cases");
        processEmailCases(
            instant, collectionExercise, survey, emailCases, actionTemplate, eventTag);
      } else {
        log.with("activeEnrolment", true)
            .with("event", eventTag)
            .error("No Template registered against the event and active enrolment");
      }
    } else {
      log.with("collectionExercise", collectionExercise)
          .with("event", eventTag)
          .info("No Emails to process");
    }
    log.debug("Getting Email cases against collectionExerciseId and event active enrolment");
    List<ActionCase> letterCases =
        actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false);
    if (!letterCases.isEmpty()) {
      ActionTemplate actionTemplate = actionTemplateService.mapEventTagToTemplate(eventTag, false);
      if (actionTemplate != null) {
        log.with("Letter cases", letterCases.size()).info("Processing letter cases");
        processLetterCases(
            letterCases, actionTemplate, survey, collectionExercise, instant, true, eventTag);
      } else {
        log.with("activeEnrolment", false)
            .with("event", eventTag)
            .error("No Template registered against the event and active enrolment");
      }
    } else {
      log.with("collectionExercise", collectionExercise)
          .with("event", eventTag)
          .info("No Letters to process");
    }
  }

  private void partialProcessCheckPoint(
      UUID collectionExerciseId,
      String eventTag,
      Optional<ActionEventPartialEntry> partialEntry,
      Instant instant) {
    Long numberOfActionCases =
        actionCaseRepository.countByCollectionExerciseId(collectionExerciseId);
    Long numberOfCases = caseSvcClientService.getNumberOfCases(collectionExerciseId);
    boolean isPresent = partialEntry.isPresent();
    if (numberOfCases == null) {
      log.error("System Error! Case service did not return case count.");
    } else {
      Long numberOfCasesToBeProcessed = numberOfCases - numberOfActionCases;
      if (isPresent) {
        ActionEventPartialEntry actionEventPartialEntry = partialEntry.get();
        actionEventPartialEntry.setLastProcessedTimestamp(Timestamp.from(instant));
        actionEventPartialEntry.setProcessedCases(numberOfActionCases);
        actionEventPartialEntry.setPendingCases(numberOfCasesToBeProcessed);
        if (numberOfCasesToBeProcessed == 0) {
          actionEventPartialEntry.setStatus(
              ActionEventPartialEntry.ActionEventPartialProcessStatus.COMPLETED);
        }
        actionEventPartialEntryRepository.save(actionEventPartialEntry);
      } else {
        if (numberOfCases - numberOfActionCases > 0) {
          ActionEventPartialEntry actionEventPartialEntry =
              ActionEventPartialEntry.builder()
                  .collectionExerciseId(collectionExerciseId)
                  .eventTag(eventTag)
                  .status(ActionEventPartialEntry.ActionEventPartialProcessStatus.PARTIAL)
                  .pendingCases(numberOfCasesToBeProcessed)
                  .processedCases(numberOfActionCases)
                  .lastProcessedTimestamp(Timestamp.from(instant))
                  .build();
          actionEventPartialEntryRepository.save(actionEventPartialEntry);
        }
      }
    }
  }

  private void processEmailCases(
      Instant instant,
      CollectionExerciseDTO collectionExercise,
      SurveyDTO survey,
      List<ActionCase> email_cases,
      ActionTemplate actionTemplate,
      String eventTag) {
    email_cases
        .parallelStream()
        .filter(c -> isActionable(c, actionTemplate, eventTag))
        .forEach(
            c ->
                processEmailCase(
                    c, collectionExercise, survey, actionTemplate, null, instant, eventTag));
  }

  /**
   * processes letter cases. populates letter entries for case parties which are actionable.
   *
   * @param letter_cases
   * @param actionTemplate
   * @param survey
   * @param collectionExercise
   */
  private boolean processLetterCases(
      List<ActionCase> letter_cases,
      ActionTemplate actionTemplate,
      SurveyDTO survey,
      CollectionExerciseDTO collectionExercise,
      Instant instant,
      boolean isNotRetry,
      String eventTag) {
    log.with("no. of cases", letter_cases.size())
        .with("actionType", actionTemplate.getType())
        .info("Populating letter data for letter cases.");
    List<LetterEntry> letterEntries =
        letter_cases
            .parallelStream()
            .filter(c -> isActionable(c, actionTemplate, eventTag))
            .map(c -> getPrintFileEntry(c, actionTemplate, survey))
            .collect(Collectors.toList());
    log.with("no. of cases", letter_cases.size())
        .with("actionType", actionTemplate.getType())
        .info("Finished populating letter data for letter cases.");
    if (letterEntries.size() == 0) {
      log.with("no. of cases", letter_cases.size())
          .with("actionType", actionTemplate.getType())
          .with("collection exercise", collectionExercise.getId())
          .info(
              "No actionable cases found against the action type for collection exercise. Hence nothing to do");
      return false;
    }
    String fileNamePrefix =
        FilenamePrefix.getPrefix(actionTemplate.getPrefix())
            + "_"
            + survey.getSurveyRef()
            + "_"
            + getExerciseRefWithoutSurveyRef(collectionExercise.getExerciseRef());
    final String now =
        DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")
            .withZone(ZoneId.systemDefault())
            .format(instant);
    String filename = String.format("%s_%s.csv", fileNamePrefix, now);
    log.info("filename: " + filename + ", uploading file");
    log.with("actionType", actionTemplate.getType()).info("Processing Print File");
    boolean isSuccess = printFileService.processPrintFile(filename, letterEntries);
    log.with("file processed?", isSuccess)
        .with("actionType", actionTemplate.getType())
        .info("Recording case action event");
    if (isNotRetry) {
      letterEntries
          .parallelStream()
          .forEach(
              letterEntry ->
                  createCaseActionEvent(
                      letterEntry.getActionCaseId(),
                      letterEntry.getActionTemplateType(),
                      letterEntry.getActionTemplateHandler(),
                      isSuccess,
                      collectionExercise.getId(),
                      survey.getId(),
                      instant,
                      eventTag));
    }
    return isSuccess;
  }

  /**
   * creates ActionEvents for processed events.
   *
   * @param caseId
   * @param type
   * @param handler
   * @param status
   * @param instant
   */
  private void createCaseActionEvent(
      UUID caseId,
      String type,
      ActionTemplateDTO.Handler handler,
      boolean status,
      UUID collectionExerciseId,
      String surveyId,
      Instant instant,
      String eventTag) {
    log.with("caseId", caseId)
        .with("actionTemplateType", type)
        .with("actionTemplateHandler", handler)
        .with("status", status)
        .info("Creating a new record.");
    ActionEvent actionEvent =
        new ActionEvent()
            .builder()
            .caseId(caseId)
            .type(type)
            .handler(handler)
            .status(
                status
                    ? ActionEvent.ActionEventStatus.PROCESSED
                    : ActionEvent.ActionEventStatus.FAILED)
            .collectionExerciseId(collectionExerciseId)
            .surveyId(UUID.fromString(surveyId))
            .processedTimestamp(status ? Timestamp.from(instant) : null)
            .tag(eventTag)
            .build();
    actionEventRepository.save(actionEvent);
  }

  /**
   * updates existing failed action event with status processed
   *
   * @param existingEvent
   * @param instant
   */
  private void updateCaseActionEvent(ActionEvent existingEvent, Instant instant) {
    log.with("caseId", existingEvent.getCaseId()).info("Updating action event to processed.");
    existingEvent.setStatus(ActionEvent.ActionEventStatus.PROCESSED);
    existingEvent.setProcessedTimestamp(Timestamp.from(instant));
    actionEventRepository.save(existingEvent);
  }
  /**
   * Populates data for print file for case event
   *
   * @param actionCase
   * @param actionTemplate
   * @param survey
   * @return
   */
  private LetterEntry getPrintFileEntry(
      ActionCase actionCase, ActionTemplate actionTemplate, SurveyDTO survey) {
    log.with("caseId", actionCase.getId())
        .with("actionTemplateType", actionTemplate.getType())
        .info("Getting print file entry");
    ActionCaseParty actionCaseParty = setParties(actionCase, survey);
    String iac = actionCase.getIac();
    String sampleUnitRef = actionCase.getSampleUnitRef();
    String status = actionCase.getStatus();
    // This is only for legacy cases which is still to be processed.
    if (actionCase.getIac() == null
        && actionCase.getSampleUnitRef() == null
        && actionCase.getStatus() == null) {
      CaseDetailsDTO caseDetailsDTO = getCaseDetails(actionCase);
      status = caseDetailsDTO.getCaseGroup().getCaseGroupStatus().toString();
      iac = caseDetailsDTO.getIac();
      sampleUnitRef = caseDetailsDTO.getCaseGroup().getSampleUnitRef();
    }
    PartyDTO businessParty = actionCaseParty.getParentParty();
    Contact contact = new Contact();
    String respondentStatus = new String();
    List<PartyDTO> respondentParties = actionCaseParty.getChildParties();
    if (respondentParties.size() > 0) {
      Attributes attributes = respondentParties.get(0).getAttributes();
      contact.setEmailAddress(attributes.getEmailAddress());
      contact.setForename(attributes.getFirstName());
      contact.setSurname(attributes.getLastName());
      respondentStatus = parseRespondentStatuses(respondentParties);
      List<PartyDTO> createdRespondentParties =
          filterListByStatus(respondentParties, ActionProcessingService.CREATED);
      if (createdRespondentParties != null && createdRespondentParties.size() > 0) {
        iac = "";
      }
    }
    log.with("caseId", actionCase.getId())
        .with("actionTemplateType", actionTemplate.getType())
        .info("Finished getting print file entry");
    return new LetterEntry(
        actionCase.getId(),
        actionTemplate.getType(),
        actionTemplate.getHandler(),
        sampleUnitRef,
        iac,
        status,
        getEnrolmentStatus(actionCaseParty.getParentParty()),
        respondentStatus,
        contact,
        businessParty.getAttributes().getRegion());
  }

  private CaseDetailsDTO getCaseDetails(ActionCase actionCase) {
    return caseSvcClientService.getCaseWithIACandCaseEvents(actionCase.getId());
  }

  /** Retry method to process failed action events. */
  @Async
  public void retryFailedEvent() {
    log.info("Starting to process Failed Events");
    Instant instant = Instant.now();
    // Get list of all failed action events.
    log.info("Getting all failed action events.");
    List<ActionEvent> failedCases =
        actionEventRepository.findByStatus(ActionEvent.ActionEventStatus.FAILED);
    if (failedCases.size() > 0) {
      // Filter all failed email cases.
      log.info("Getting all failed email cases.");
      List<ActionEvent> failedEmailCases =
          failedCases
              .parallelStream()
              .filter(e -> e.getHandler() == ActionTemplateDTO.Handler.EMAIL)
              .collect(Collectors.toList());
      // Process all failed email cases.
      if (failedEmailCases.size() > 0) {
        log.with(failedCases.size()).info("starting to process all failed email cases.");
        processFailedEmailCases(failedEmailCases, instant);
      } else {
        log.info("No failed email cases to process");
      }
      // Filter all failed letter cases.
      List<ActionEvent> failedLetterCases =
          failedCases
              .parallelStream()
              .filter(e -> e.getHandler() == ActionTemplateDTO.Handler.LETTER)
              .collect(Collectors.toList());
      if (failedLetterCases.size() > 0) {
        // Group all failed letter cases with action event type
        Map<String, List<ActionEvent>> groupedByEventTypeFailedLetterCases =
            failedLetterCases.parallelStream().collect(Collectors.groupingBy(ActionEvent::getType));
        // for each action event type
        for (String eventsFailed : groupedByEventTypeFailedLetterCases.keySet()) {
          log.with("actionEventType", eventsFailed).info("Getting action template");
          ActionTemplate template = actionTemplateRepository.findByType(eventsFailed);
          // group action event against that event with collection exercise id.
          Map<UUID, List<ActionEvent>> groupedByEventTypeAndCollexFailedLetterCases =
              groupedByEventTypeFailedLetterCases
                  .get(eventsFailed)
                  .parallelStream()
                  .collect(Collectors.groupingBy(ActionEvent::getCollectionExerciseId));
          // for each distinct collection exercise process letters
          for (UUID collectionExerciseId : groupedByEventTypeAndCollexFailedLetterCases.keySet()) {
            log.with(eventsFailed).with(collectionExerciseId).info("Processing Failed Event");
            List<ActionEvent> groupedCollexFailedLetterCases =
                groupedByEventTypeAndCollexFailedLetterCases.get(collectionExerciseId);
            // Get Action Event
            ActionEvent actionEvent = groupedCollexFailedLetterCases.get(0);
            // Get Collection Exercise dto
            CollectionExerciseDTO collectionExercise =
                getCollectionExercise(collectionExerciseId);
            // Get survey dto
            SurveyDTO survey = getSurvey(actionEvent.getSurveyId().toString());
            // group all associated failed cases against the collection exercise id
            List<UUID> cases =
                groupedCollexFailedLetterCases
                    .parallelStream()
                    .map(ActionEvent::getCollectionExerciseId)
                    .collect(Collectors.toList());
            log
              .with("number of cases", cases.size())
              .with("collectionExerciseId", collectionExerciseId)
              .info("Mapped failed cases against collection exercise id");
            // Get all action cases against the ids
            List<ActionCase> actionCases = actionCaseRepository.findByIdIn(cases);
            log.with("action cases",actionCases.size())
              .with("collectionExerciseId", collectionExerciseId)
              .info("Found failed action cases to be processed");
            // process letter cases.
            boolean isSuccess =
                processLetterCases(
                    actionCases,
                    template,
                    survey,
                    collectionExercise,
                    instant,
                    false,
                    actionEvent.getTag());
            if (isSuccess) {
              // update all action event to processed
              groupedCollexFailedLetterCases
                  .parallelStream()
                  .forEach(ae -> updateCaseActionEvent(ae, instant));
            }
          }
        }
      } else {
        log.info("No failed letter cases to process");
      }
    } else {
      log.info("No fail cases to process at this time.");
    }
  }

  private void processFailedEmailCases(List<ActionEvent> failedEmailCases, Instant instant) {
    failedEmailCases
        .parallelStream()
        .forEach(
            c -> {
              log.with(c.getCaseId())
                  .with(c.getType())
                  .with(c.getHandler())
                  .with(c.getCollectionExerciseId())
                  .with(c.getSurveyId())
                  .info("Retrying failed email case.");
              ActionTemplateDTO.Handler templateHandler = c.getHandler();
              ActionCase actionCase = actionCaseRepository.findById(c.getCaseId());
              CollectionExerciseDTO collectionExercise =
                  getCollectionExercise(c.getCollectionExerciseId());
              SurveyDTO survey = getSurvey(c.getSurveyId().toString());
              ActionTemplate actionTemplate = actionTemplateRepository.findByType(c.getType());
              processEmailCase(
                  actionCase, collectionExercise, survey, actionTemplate, c, instant, c.getTag());
            });
  }

  /**
   * Processes Email Cases. Get CaseParty against the email case If BusinessNotification populate
   * email data and process it for each 'ACTIVE' respondentParty else to the Party
   *
   * @param actionCase
   * @param collectionExercise
   * @param survey
   * @param actionTemplate
   */
  private void processEmailCase(
      ActionCase actionCase,
      CollectionExerciseDTO collectionExercise,
      SurveyDTO survey,
      ActionTemplate actionTemplate,
      ActionEvent actionEvent,
      Instant instant,
      String eventTag) {
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
      ActionCaseParty actionCaseParty = setParties(actionCase, survey);
      if (isBusinessNotification(actionCase)) {
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
      createCaseActionEvent(
          actionCaseId,
          templateType,
          templateHandler,
          isSuccess,
          collectionExercise.getId(),
          survey.getId(),
          instant,
          eventTag);
    } else {
      updateCaseActionEvent(actionEvent, instant);
    }
  }

  private boolean isActive(PartyDTO respondentParty) {
    log.with("respondentParty", respondentParty.getStatus()).info("Check is respondent active");
    return respondentParty.getStatus() == "ACTIVE" ? true : false;
  }

  /**
   * Check if the sample type associated to case is of type B
   *
   * @param actionCase
   * @return
   */
  private boolean isBusinessNotification(ActionCase actionCase) {
    return actionCase.getSampleUnitType().equals(SampleUnitDTO.SampleUnitType.B.name());
  }

  /**
   * Gets collection exercise dto against collection exercise id
   *
   * @param collectionExerciseId
   * @return
   */
  private CollectionExerciseDTO getCollectionExercise(UUID collectionExerciseId) {
    log.with("collectionExerciseId", collectionExerciseId).debug("Getting collectionExercise");
    return collectionExerciseClientService.getCollectionExercise(collectionExerciseId);
  }

  /**
   * Gets survey dto against survey id
   *
   * @param surveyId
   * @return
   */
  private SurveyDTO getSurvey(String surveyId) {
    log.with("surveyId", surveyId).debug("Getting survey");
    return surveySvcClientService.requestDetailsForSurvey(surveyId);
  }

  /**
   * Gets Business Party and associated respondent parties
   *
   * @param actionCase
   * @param survey
   * @return
   */
  private ActionCaseParty setParties(ActionCase actionCase, SurveyDTO survey) {
    log.with("caseId", actionCase.getId())
        .with("surveyId", survey.getId())
        .info("Getting Event Party data");
    List<String> desiredEnrolmentStatuses = new ArrayList<>();
    desiredEnrolmentStatuses.add("ENABLED");
    desiredEnrolmentStatuses.add("PENDING");
    log.info("Getting parent party data");
    PartyDTO businessParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SampleUnitDTO.SampleUnitType.B.name(),
            actionCase.getPartyId(),
            survey.getId(),
            desiredEnrolmentStatuses);
    log.info("Getting child party data");
    List<PartyDTO> respondentParties = getRespondentParties(businessParty);
    log.with("caseId", actionCase.getId())
        .with("surveyId", survey.getId())
        .info("Finish getting Event Party data");
    return new ActionCaseParty(businessParty, respondentParties);
  }

  /**
   * gets respondent parties for business party
   *
   * @param businessParty
   * @return
   */
  private List<PartyDTO> getRespondentParties(PartyDTO businessParty) {
    log.info("getting respondent party");
    final List<String> respondentPartyIds =
        businessParty
            .getAssociations()
            .stream()
            .map(Association::getPartyId)
            .collect(Collectors.toList());
    return respondentPartyIds
        .stream()
        .map(
            id ->
                partySvcClientService.getParty(
                    SampleUnitDTO.SampleUnitType.BI.toString(), UUID.fromString(id)))
        .collect(Collectors.toList());
  }

  /**
   * * checks if the case has already been processed
   *
   * @param actionCase
   * @param actionTemplate
   * @return
   */
  private boolean isActionable(
      ActionCase actionCase, ActionTemplate actionTemplate, String eventTag) {
    ActionEvent actionEvent =
        actionEventRepository.findByCaseIdAndTypeAndHandlerAndTagAndStatus(
            actionCase.getId(),
            actionTemplate.getType(),
            actionTemplate.getHandler(),
            eventTag,
            ActionEvent.ActionEventStatus.PROCESSED);
    if (actionEvent == null) {
      log.with("actionCase", actionCase.getId())
          .with("handler", actionTemplate.getHandler())
          .info("Event Already processed.");
    }
    return actionEvent == null;
  }

  /**
   * Processes Email. Populates email data NotifyModel
   *
   * @param businessParty
   * @param respondentParty
   * @param survey
   * @param actionTemplate
   * @param actionCase
   * @param collectionExercise
   */
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
      CaseDetailsDTO caseDetailsDTO = getCaseDetails(actionCase);
      sampleUnitRef = caseDetailsDTO.getCaseGroup().getSampleUnitRef();
    }
    Classifiers classifiers = getClassifiers(businessParty, survey, actionTemplate);
    Personalisation personalisation =
        getPersonalisation(
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

  /**
   * * gets email personalisation data
   *
   * @param businessParty
   * @param respondentParty
   * @param survey
   * @param sampleUnitRef
   * @param collectionExercise
   * @return
   */
  private Personalisation getPersonalisation(
      PartyDTO businessParty,
      PartyDTO respondentParty,
      SurveyDTO survey,
      String sampleUnitRef,
      CollectionExerciseDTO collectionExercise) {
    log.info("collecting personalisation for email");
    DateFormat dateFormat =
        new SimpleDateFormat(ActionProcessingService.DATE_FORMAT_IN_REMINDER_EMAIL);
    Personalisation personalisation =
        Personalisation.builder()
            .firstname(respondentParty.getAttributes().getFirstName())
            .lastname(respondentParty.getAttributes().getLastName())
            .reportingUnitReference(sampleUnitRef)
            .returnByDate(dateFormat.format(collectionExercise.getScheduledReturnDateTime()))
            .tradingSyle(generateTradingStyle(businessParty.getAttributes()))
            .ruName(businessParty.getName())
            .surveyId(survey.getSurveyRef())
            .surveyName(survey.getLongName())
            .respondentPeriod(collectionExercise.getUserDescription())
            .build();
    return personalisation;
  }

  /**
   * gets classifiers data for the email
   *
   * @param businessParty
   * @param survey
   * @param actionTemplate
   * @return
   */
  private Classifiers getClassifiers(
      PartyDTO businessParty, SurveyDTO survey, ActionTemplate actionTemplate) {
    log.info("collecting classifiers for email");
    Classifiers classifiers =
        Classifiers.builder()
            .actionType(actionTemplate.getType())
            .legalBasis(survey.getLegalBasis())
            .region(businessParty.getAttributes().getRegion())
            .surveyRef(survey.getSurveyRef())
            .build();
    return classifiers;
  }

  private String generateTradingStyle(final Attributes businessUnitAttributes) {
    log.info("Generate trading style");
    final List<String> tradeStyles =
        Arrays.asList(
            businessUnitAttributes.getTradstyle1(),
            businessUnitAttributes.getTradstyle2(),
            businessUnitAttributes.getTradstyle3());
    return tradeStyles.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
  }

  private String getEnrolmentStatus(final PartyDTO parentParty) {
    log.info("getting enrolment statuss");
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

  private String parseRespondentStatuses(final List<PartyDTO> childParties) {
    log.info("Getting respondent status");
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

  private List<PartyDTO> filterListByStatus(List<PartyDTO> parties, String status) {
    log.info("filter parties by status");
    return parties == null
        ? null
        : parties.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
  }

  private String getExerciseRefWithoutSurveyRef(String exerciseRef) {
    log.info("get exercise ref without survey ref");
    String exerciseRefWithoutSurveyRef = StringUtils.substringAfter(exerciseRef, "_");
    return StringUtils.defaultIfEmpty(exerciseRefWithoutSurveyRef, exerciseRef);
  }
}
