package uk.gov.ons.ctp.response.action.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;

@RunWith(MockitoJUnitRunner.class)
public class ActionStateServiceTest {
  private static final String ACTION_STATE_TRANSITION_ERROR_MSG = "Action State transition failed.";

  @InjectMocks private ActionStateService actionStateService;

  @Mock private ActionRepository actionRepo;

  @Mock
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  @Test
  public void testTransition() throws Exception {
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenReturn(ActionDTO.ActionState.COMPLETED);

    Action action = new Action();
    action.setState(ActionDTO.ActionState.SUBMITTED);
    actionStateService.transitionAction(action, ActionDTO.ActionEvent.REQUEST_ACCEPTED);

    verify(actionRepo, times(1)).saveAndFlush(any(Action.class));
  }

  @Test(expected = CTPException.class)
  public void testTransitionThrowsException() throws Exception {
    when(actionSvcStateTransitionManager.transition(
            any(ActionDTO.ActionState.class), any(ActionDTO.ActionEvent.class)))
        .thenThrow(
            new CTPException(CTPException.Fault.SYSTEM_ERROR, ACTION_STATE_TRANSITION_ERROR_MSG));

    Action action = new Action();
    action.setState(ActionDTO.ActionState.SUBMITTED);
    actionStateService.transitionAction(action, ActionDTO.ActionEvent.REQUEST_ACCEPTED);

    verify(actionRepo, never()).saveAndFlush(any(Action.class));
  }
}
