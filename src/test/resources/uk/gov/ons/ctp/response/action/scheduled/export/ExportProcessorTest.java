package uk.gov.ons.ctp.response.action.scheduled.export;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;

import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.service.NotificationFileCreator;


@RunWith(MockitoJUnitRunner.class)
public class ExportProcessorTest {
  @Mock private ActionRequestRepository actionRequestRepository;
  @Mock private NotificationFileCreator notificationFileCreator;
  @Mock private ExportJobRepository exportJobRepository;

  @InjectMocks private PrintFileProcessor exportProcessor;

  @Test
  public void testHappyPath() {
    ExportJob exportJob = new ExportJob(UUID.randomUUID());
    String actionType = "BSNOT";

    ActionRequestInstruction ari = new ActionRequestInstruction();
    ari.setActionId(UUID.randomUUID());
    ari.setActionType(actionType);
    ari.setSurveyRef("SURVEYREF");
    ari.setExerciseRef("EXERCISEREF");
    ari.setResponseRequired(true);

    List<ActionRequestInstruction> actionRequestInstructions = Collections.singletonList(ari);

    // Given
    given(actionRequestRepository.existsByExportJobIdIsNull()).willReturn(true);
    given(exportJobRepository.saveAndFlush(any())).willReturn(exportJob);

    given(actionRequestRepository.findByExportJobId(any()))
        .willReturn(actionRequestInstructions.stream());

    // When
    exportProcessor.processExport();

    // Verify
    verify(exportJobRepository).saveAndFlush(any());
    verify(actionRequestRepository).updateActionsWithExportJob(eq(exportJob.getId()));
    verify(actionRequestRepository).findByExportJobId(eq(exportJob.getId()));

    verify(notificationFileCreator)
        .uploadData(
            eq("BSNOT_SURVEYREF_EXERCISEREF"), eq(actionRequestInstructions), eq(exportJob));
  }
}
