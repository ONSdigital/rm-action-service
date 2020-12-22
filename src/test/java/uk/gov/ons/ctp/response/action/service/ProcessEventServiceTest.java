package uk.gov.ons.ctp.response.action.service;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.client.SurveySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionEventRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;

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

  @InjectMocks private ProcessEventService processEventService;
  UUID collectionExerciseId = UUID.randomUUID();
  UUID surveyId = UUID.randomUUID();

  @Before
  public void setUp() throws Exception {
    CollectionExerciseDTO collectionExerciseDTO = new CollectionExerciseDTO();
    collectionExerciseDTO.setId(collectionExerciseId);
    collectionExerciseDTO.setSurveyId(surveyId.toString());
    collectionExerciseDTO.setSurveyRef("1234");
    collectionExerciseDTO.setUserDescription("Test");
    ActionTemplate template = new ActionTemplate();
    template.setType("BSNE");
    template.setDescription("test");
    template.setHandler(ActionTemplateDTO.Handler.EMAIL);

    Mockito.when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(collectionExerciseDTO);
    // Mockito.when(actionCaseRepository.findByCollectionExerciseIdAndActiveEnrolment(collectionExerciseId, true)).thenReturn();

  }

  @Test
  public void noEmailOrLetterToBeProcessed() {
    processEventService.processEvents(collectionExerciseId, "go_live");
    Mockito.verify(partySvcClientService, Mockito.never())
        .getPartyWithAssociationsFilteredBySurvey(
            Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
    Mockito.verify(emailService, Mockito.never()).processEmail(Mockito.anyObject());
    Mockito.verify(printFileService, Mockito.never())
        .processPrintFile(Mockito.anyObject(), Mockito.anyObject());
  }
}
