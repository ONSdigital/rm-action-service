package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.time.DateTimeUtil;

@Service
public class ActionStateService {

  private static final Logger LOG = LoggerFactory.getLogger(ActionStateService.class);

  @Autowired private ActionRepository actionRepo;

  @Autowired
  private StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  /**
   * Change the action status in db to indicate we have sent this action downstream, and clear
   * previous situation (in the scenario where the action has prev. failed)
   *
   * @param action the action to change and persist
   * @param event the event to transition the action with
   * @throws CTPException if action state transition error
   */
  public void transitionAction(final Action action, final ActionDTO.ActionEvent event)
      throws CTPException {
    transitionState(event, action);
    actionRepo.saveAndFlush(action);
  }

  public void loadAndTransitionActions(
      final List<UUID> actionIds, final ActionDTO.ActionEvent event) throws CTPException {
    List<Action> actions = actionRepo.findByIdIn(actionIds);
    for (Action action : actions) {
      transitionState(event, action);
    }
    actionRepo.save(actions);
    actionRepo.flush();
  }

  public void transitionActions(final List<Action> actions, final ActionDTO.ActionEvent event)
      throws CTPException {
    for (Action action : actions) {
      transitionState(event, action);
    }
    actionRepo.save(actions);
    actionRepo.flush();
  }

  private void transitionState(ActionDTO.ActionEvent event, Action action) throws CTPException {
    ActionDTO.ActionState nextState =
        actionSvcStateTransitionManager.transition(action.getState(), event);
    LOG.with("action id", action.getId()).with("state", nextState).debug("transition state");
    action.setState(nextState);
    action.setSituation(null);
    action.setUpdatedDateTime(DateTimeUtil.nowUTC());
  }
}
