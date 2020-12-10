package uk.gov.ons.ctp.response.action.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@RunWith(MockitoJUnitRunner.class)
public class NotificationFileCreatorTest {

  private static final SimpleDateFormat FILENAME_DATE_FORMAT =
      new SimpleDateFormat("ddMMyyyy_HHmm");

  @Mock private Clock clock;
  @Mock private PrintFileService printFileService;
  @Mock private ActionStateService actionStateService;
  @InjectMocks private NotificationFileCreator notificationFileCreator;

  @Test
  public void shouldCreateTheCorrectFilename() throws Exception {
    String actionType = "ACTIONTYPE";
    ActionRequest ari = new ActionRequest();
    UUID actionUUID = UUID.randomUUID();
    ari.setActionId(actionUUID.toString());
    ari.setActionType(actionType);
    ari.setSurveyRef("SURVEYREF");
    ari.setExerciseRef("EXERCISEREF");
    ari.setResponseRequired(true);

    List<UUID> actions = new ArrayList<>();
    actions.add(actionUUID);

    List<ActionRequest> actionRequests = Collections.singletonList(ari);

    Date now = new Date();

    // Given
    String expectedFilename = String.format("BSNOT_%s.csv", FILENAME_DATE_FORMAT.format(now));
    given(clock.millis()).willReturn(now.getTime());
    given(printFileService.send(expectedFilename, actionRequests)).willReturn(true);

    // When
    notificationFileCreator.uploadData(
        ActionDTO.ActionEvent.REQUEST_DISTRIBUTED, "BSNOT", actionRequests);

    // Then
    verify(printFileService).send(expectedFilename, actionRequests);
    verify(actionStateService, times(1))
        .loadAndTransitionActions(actions, ActionDTO.ActionEvent.REQUEST_DISTRIBUTED);
  }

  /** An exception is thrown when transitioning the state of the Action */
  @Test(expected = IllegalStateException.class)
  public void testActionStateTransitionThrowsException() throws CTPException {

    String actionType = "ACTIONTYPE";
    ActionRequest ari = new ActionRequest();
    UUID actionUUID = UUID.randomUUID();
    ari.setActionId(actionUUID.toString());
    ari.setActionType(actionType);
    ari.setSurveyRef("SURVEYREF");
    ari.setExerciseRef("EXERCISEREF");
    ari.setResponseRequired(true);

    List<UUID> actions = new ArrayList<>();
    actions.add(actionUUID);

    List<ActionRequest> actionRequests = Collections.singletonList(ari);

    Date now = new Date();

    // Given
    String expectedFilename = String.format("BSNOT_%s.csv", FILENAME_DATE_FORMAT.format(now));
    given(clock.millis()).willReturn(now.getTime());
    given(printFileService.send(expectedFilename, actionRequests)).willReturn(true);
    doThrow(new CTPException(CTPException.Fault.SYSTEM_ERROR, "action state transition failed"))
        .when(actionStateService)
        .loadAndTransitionActions(actions, ActionDTO.ActionEvent.REQUEST_DISTRIBUTED);

    // When
    notificationFileCreator.uploadData(
        ActionDTO.ActionEvent.REQUEST_DISTRIBUTED, "BSNOT", actionRequests);
  }
}
