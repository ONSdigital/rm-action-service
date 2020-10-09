package uk.gov.ons.ctp.response.action.state;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.lib.common.state.BasicStateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManagerFactory;

/**
 * This is the state transition manager factory for the actionsvc. It intended that this will be
 * refactored into a common framework class and that it initialises each entities manager from
 * database held transitions.
 */
@Component
public class ActionSvcStateTransitionManagerFactory implements StateTransitionManagerFactory {

  public static final String ACTION_ENTITY = "Action";

  private Map<String, StateTransitionManager<?, ?>> managers;

  /** Create and init the factory with concrete StateTransitionManagers for each required entity */
  public ActionSvcStateTransitionManagerFactory() {
    managers = new HashMap<>();

    final Map<ActionState, Map<ActionEvent, ActionState>> transitions = new HashMap<>();

    // SUBMITTED
    final Map<ActionEvent, ActionState> transitionMapForSubmitted = new HashMap<>();
    transitionMapForSubmitted.put(ActionEvent.REQUEST_DISTRIBUTED, ActionState.PENDING);

    transitionMapForSubmitted.put(ActionEvent.REQUEST_COMPLETED, ActionState.COMPLETED);

    transitionMapForSubmitted.put(ActionEvent.REQUEST_CANCELLED, ActionState.ABORTED);

    transitions.put(ActionState.SUBMITTED, transitionMapForSubmitted);

    // PENDING
    final Map<ActionEvent, ActionState> transitionMapForPending = new HashMap<>();
    transitionMapForPending.put(ActionEvent.REQUEST_FAILED, ActionState.SUBMITTED);

    transitionMapForPending.put(ActionEvent.REQUEST_ACCEPTED, ActionState.ACTIVE);

    transitionMapForPending.put(ActionEvent.REQUEST_DECLINED, ActionState.DECLINED);

    transitionMapForPending.put(ActionEvent.REQUEST_COMPLETED, ActionState.COMPLETED);
    transitionMapForPending.put(ActionEvent.REQUEST_COMPLETED_DEACTIVATE, ActionState.COMPLETED);
    transitionMapForPending.put(ActionEvent.REQUEST_COMPLETED_DISABLE, ActionState.COMPLETED);
    transitions.put(ActionState.PENDING, transitionMapForPending);

    // ACTIVE
    final Map<ActionEvent, ActionState> transitionMapForActive = new HashMap<>();
    transitionMapForActive.put(ActionEvent.REQUEST_FAILED, ActionState.SUBMITTED);

    transitionMapForActive.put(ActionEvent.REQUEST_COMPLETED, ActionState.COMPLETED);
    transitionMapForActive.put(ActionEvent.REQUEST_COMPLETED_DEACTIVATE, ActionState.COMPLETED);
    transitionMapForActive.put(ActionEvent.REQUEST_COMPLETED_DISABLE, ActionState.COMPLETED);
    transitions.put(ActionState.ACTIVE, transitionMapForActive);

    // COMPLETED
    final Map<ActionEvent, ActionState> transitionMapForCompleted = new HashMap<>();
    transitionMapForCompleted.put(ActionEvent.REQUEST_CANCELLED, ActionState.COMPLETED);
    transitions.put(ActionState.COMPLETED, transitionMapForCompleted);

    // RERUN
    final Map<ActionEvent, ActionState> transitionMapForReRun = new HashMap<>();
    transitionMapForReRun.put(ActionEvent.REQUEST_RERUN, ActionState.SUBMITTED);
    transitions.put(ActionState.ABORTED, transitionMapForReRun);

    final StateTransitionManager<ActionState, ActionEvent> actionStateTransitionManager =
        new BasicStateTransitionManager<>(transitions);

    managers.put(ACTION_ENTITY, actionStateTransitionManager);
  }

  /*
   * (non-Javadoc)
   * @see uk.gov.ons.ctp.response.action.state.StateTransitionManagerFactory#
   * getStateTransitionManager(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public StateTransitionManager<?, ?> getStateTransitionManager(final String entity) {
    return managers.get(entity);
  }
}
