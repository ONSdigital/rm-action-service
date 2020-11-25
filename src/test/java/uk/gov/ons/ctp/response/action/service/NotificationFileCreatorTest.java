package uk.gov.ons.ctp.response.action.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

@RunWith(MockitoJUnitRunner.class)
public class NotificationFileCreatorTest {

  private static final SimpleDateFormat FILENAME_DATE_FORMAT =
      new SimpleDateFormat("ddMMyyyy_HHmm");

  @Mock private Clock clock;
  @Mock private PrintFileService printFileService;
  @InjectMocks private NotificationFileCreator notificationFileCreator;

  @Test
  public void shouldCreateTheCorrectFilename() {
    String actionType = "ACTIONTYPE";
    ActionRequest ari = new ActionRequest();
    ari.setActionId(UUID.randomUUID().toString());
    ari.setActionType(actionType);
    ari.setSurveyRef("SURVEYREF");
    ari.setExerciseRef("EXERCISEREF");
    ari.setResponseRequired(true);

    List<ActionRequest> actionRequestInstructions = Collections.singletonList(ari);

    Date now = new Date();

    // Given
    String expectedFilename = String.format("BSNOT_%s.csv", FILENAME_DATE_FORMAT.format(now));
    given(clock.millis()).willReturn(now.getTime());
    given(printFileService.send(expectedFilename, actionRequestInstructions)).willReturn(true);

    // When
    notificationFileCreator.uploadData("BSNOT", actionRequestInstructions);

    // Then
    verify(printFileService).send(expectedFilename, actionRequestInstructions);
  }

  @Test
  public void shouldThrowExceptionForDuplicateFilename() {
    String actionType = "ACTIONTYPE";
    ActionRequest ari = new ActionRequest();
    ari.setActionId(UUID.randomUUID().toString());
    ari.setActionType(actionType);
    ari.setSurveyRef("SURVEYREF");
    ari.setExerciseRef("EXERCISEREF");
    ari.setResponseRequired(true);

    List<ActionRequest> actionRequestInstructions = Collections.singletonList(ari);
    Date now = new Date();
    boolean expectedExceptionThrown = false;

    // Given
    given(clock.millis()).willReturn(now.getTime());

    // When
    try {
      notificationFileCreator.uploadData("BSNOT", actionRequestInstructions);
    } catch (RuntimeException ex) {
      expectedExceptionThrown = true;
    }

    // Then
    assertThat(expectedExceptionThrown).isTrue();
  }
}
