package uk.gov.ons.ctp.response.action.service;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.client.SurveySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionEvent;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionEventRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventServiceTest {
  @Mock private ActionTemplateService actionTemplateService;
  @Mock private ActionCaseRepository actionCaseRepository;
  @Mock private ActionEventRepository actionEventRepository;
  @Mock private CollectionExerciseClientService collectionExerciseClientService;
  @Mock private SurveySvcClientService surveySvcClientService;
  @Mock private PartySvcClientService partySvcClientService;
  @Mock private NotifyLetterService printFileService;
  @Mock private NotifyEmailService emailService;
  @Mock private ActionTemplateRepository actionTemplateRepository;
  @Mock private CaseSvcClientService caseSvcClientService;

  @InjectMocks private ProcessEventService processEventService;

  private ProcessEventServiceTestData testData = new ProcessEventServiceTestData();

  @Test
  public void noEmailOrLetterToBeProcessed() {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    Mockito.when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    Mockito.when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    processEventService.processEvents(collectionExerciseId, "go_live");
    verify(partySvcClientService, never())
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(emailService, never()).processEmail(anyObject());
    verify(printFileService, never()).processPrintFile(anyObject(), anyObject());
  }

  @Test
  public void onlyEmailToBeProcessed() {
    // Given
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    List<ActionCase> actionCases = new ArrayList<>();
    actionCases.add(
        testData.setupActionCase(
            caseId,
            true,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));
    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, true))
        .thenReturn(actionCases);

    when(actionTemplateService.mapEventTagToTemplate("go_live", true))
        .thenReturn(
            testData.setupActionTemplate("BSNE", ActionTemplateDTO.Handler.EMAIL, "go_live"));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject()))
        .thenReturn(
            testData.setupBusinessParty(
                "1", "YY", "test", "test@test.com", respondentId.toString()));

    when(partySvcClientService.getParty(anyObject(), anyObject()))
        .thenReturn(
            testData.setupRespondentParty(
                "test", "test", "test@test.com", respondentId.toString()));

    // When
    processEventService.processEvents(collectionExerciseId, "go_live");

    // Then
    verify(partySvcClientService, atMost(2))
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(printFileService, never()).processPrintFile(anyObject(), anyObject());
    verify(emailService, atLeastOnce()).processEmail(anyObject());
  }

  @Test
  public void onlyLetterToBeProcessed() {
    // Given
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    List<ActionCase> actionCases = new ArrayList<>();
    actionCases.add(
        testData.setupActionCase(
            caseId,
            false,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));
    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false))
        .thenReturn(actionCases);

    when(actionTemplateService.mapEventTagToTemplate("mps", false))
        .thenReturn(testData.setupActionTemplate("BSNE", ActionTemplateDTO.Handler.EMAIL, "mps"));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject()))
        .thenReturn(
            testData.setupBusinessParty(
                "1", "YY", "test", "test@test.com", respondentId.toString()));

    when(partySvcClientService.getParty(anyObject(), anyObject()))
        .thenReturn(
            testData.setupRespondentParty(
                "test", "test", "test@test.com", respondentId.toString()));

    // When
    processEventService.processEvents(collectionExerciseId, "mps");

    // Then
    verify(partySvcClientService, atMost(2))
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(printFileService, atLeastOnce()).processPrintFile(anyObject(), anyObject());
    verify(emailService, never()).processEmail(anyObject());
  }

  @Test
  public void failedLetterToBeProcessed() {
    // Given
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    List<ActionEvent> actionEvents = new ArrayList<>();
    actionEvents.add(
        testData.setActionEvent(
            caseId,
            surveyId,
            collectionExerciseId,
            ActionEvent.ActionEventStatus.FAILED,
            ActionTemplateDTO.Handler.LETTER,
            "BSNE"));
    when(actionEventRepository.findByStatus(ActionEvent.ActionEventStatus.FAILED))
        .thenReturn(actionEvents);
    when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    List<ActionCase> actionCases = new ArrayList<>();
    actionCases.add(
        testData.setupActionCase(
            caseId,
            false,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));
    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false))
        .thenReturn(actionCases);
    when(actionTemplateRepository.findByType(anyObject()))
        .thenReturn(testData.setupActionTemplate("BSNE", ActionTemplateDTO.Handler.EMAIL, "mps"));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject()))
        .thenReturn(
            testData.setupBusinessParty(
                "1", "YY", "test", "test@test.com", respondentId.toString()));

    when(partySvcClientService.getParty(anyObject(), anyObject()))
        .thenReturn(
            testData.setupRespondentParty(
                "test", "test", "test@test.com", respondentId.toString()));

    // When
    processEventService.retryFailedEvent();

    // Then
    verify(partySvcClientService, atMost(2))
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(printFileService, atLeastOnce()).processPrintFile(anyObject(), anyObject());
    verify(emailService, never()).processEmail(anyObject());
  }

  @Test
  public void failedEmailToBeProcessed() {
    // Given
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    List<ActionEvent> actionEvents = new ArrayList<>();
    actionEvents.add(
        testData.setActionEvent(
            caseId,
            surveyId,
            collectionExerciseId,
            ActionEvent.ActionEventStatus.FAILED,
            ActionTemplateDTO.Handler.EMAIL,
            "BSNE"));
    when(actionEventRepository.findByStatus(ActionEvent.ActionEventStatus.FAILED))
        .thenReturn(actionEvents);
    when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    List<ActionCase> actionCases = new ArrayList<>();
    actionCases.add(
        testData.setupActionCase(
            caseId,
            true,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));
    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, true))
        .thenReturn(actionCases);

    when(actionTemplateService.mapEventTagToTemplate("go_live", true))
        .thenReturn(
            testData.setupActionTemplate("BSNE", ActionTemplateDTO.Handler.EMAIL, "go_live"));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject()))
        .thenReturn(
            testData.setupBusinessParty(
                "1", "YY", "test", "test@test.com", respondentId.toString()));

    when(partySvcClientService.getParty(anyObject(), anyObject()))
        .thenReturn(
            testData.setupRespondentParty(
                "test", "test", "test@test.com", respondentId.toString()));

    // When
    processEventService.processEvents(collectionExerciseId, "go_live");

    // Then
    verify(partySvcClientService, atMost(2))
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(printFileService, never()).processPrintFile(anyObject(), anyObject());
    verify(emailService, atLeastOnce()).processEmail(anyObject());
  }

  @Test
  public void onlyLetterToBeProcessedForLegacyCollectionExercise() {
    // Given
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    List<ActionCase> actionCases = new ArrayList<>();
    actionCases.add(
        testData.setupActionCase(
            caseId,
            false,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            null,
            null));
    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false))
        .thenReturn(actionCases);

    when(actionTemplateService.mapEventTagToTemplate("mps", false))
        .thenReturn(testData.setupActionTemplate("BSNE", ActionTemplateDTO.Handler.EMAIL, "mps"));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject()))
        .thenReturn(
            testData.setupBusinessParty(
                "1", "YY", "test", "test@test.com", respondentId.toString()));

    when(partySvcClientService.getParty(anyObject(), anyObject()))
        .thenReturn(
            testData.setupRespondentParty(
                "test", "test", "test@test.com", respondentId.toString()));

    CaseDetailsDTO caseDetailsDTO = new CaseDetailsDTO();
    CaseGroupDTO caseGroupDTO = new CaseGroupDTO();
    caseDetailsDTO.setIac("jhsdkfhkdsf");
    caseGroupDTO.setSampleUnitRef("4000006");
    caseGroupDTO.setCaseGroupStatus(CaseGroupStatus.INPROGRESS);
    caseDetailsDTO.setCaseGroup(caseGroupDTO);
    when(caseSvcClientService.getCaseWithIACandCaseEvents(anyObject())).thenReturn(caseDetailsDTO);

    // When
    processEventService.processEvents(collectionExerciseId, "mps");

    // Then
    verify(partySvcClientService, atMost(2))
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(printFileService, atLeastOnce()).processPrintFile(anyObject(), anyObject());
    verify(emailService, never()).processEmail(anyObject());
    verify(caseSvcClientService, atMost(1)).generateNewIacForCase(anyObject());
  }

  @Test
  public void bothEmailAndLetterToBeProcessed() {
    // Given
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    when(surveySvcClientService.requestDetailsForSurvey(any()))
        .thenReturn(testData.setupSurveyDTO(surveyId, "test", "400000005", "test"));
    List<ActionCase> letterCases = new ArrayList<>();
    letterCases.add(
        testData.setupActionCase(
            caseId,
            false,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));

    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false))
        .thenReturn(letterCases);

    List<ActionCase> EmailCases = new ArrayList<>();
    EmailCases.add(
        testData.setupActionCase(
            caseId,
            true,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));

    when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, true))
        .thenReturn(EmailCases);

    when(actionTemplateService.mapEventTagToTemplate("reminder", false))
        .thenReturn(
            testData.setupActionTemplate("BSNL", ActionTemplateDTO.Handler.LETTER, "reminder"));

    when(actionTemplateService.mapEventTagToTemplate("reminder", true))
        .thenReturn(
            testData.setupActionTemplate("BSNE", ActionTemplateDTO.Handler.EMAIL, "reminder"));

    when(partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject()))
        .thenReturn(
            testData.setupBusinessParty(
                "1", "YY", "test", "test@test.com", respondentId.toString()));

    when(partySvcClientService.getParty(anyObject(), anyObject()))
        .thenReturn(
            testData.setupRespondentParty(
                "test", "test", "test@test.com", respondentId.toString()));

    // When
    processEventService.processEvents(collectionExerciseId, "reminder");

    // Then
    verify(partySvcClientService, atMost(4))
        .getPartyWithAssociationsFilteredBySurvey(
            anyObject(), anyObject(), anyObject(), anyObject());
    verify(printFileService, atLeastOnce()).processPrintFile(anyObject(), anyObject());
    verify(emailService, atLeastOnce()).processEmail(anyObject());
  }
}
