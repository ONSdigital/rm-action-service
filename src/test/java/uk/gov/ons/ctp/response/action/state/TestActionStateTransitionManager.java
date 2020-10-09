package uk.gov.ons.ctp.response.action.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManagerFactory;

/**
 * A test of the state transition manager It simply has to test a single good and a single bad
 * transition - all it is testing is the underlying mechanism, not a real implementation, where we
 * will want to assert all of the valid and invalid transitions
 */
public class TestActionStateTransitionManager {

  private static final int TIMEOUT = 10000;
  private static final int INVOCATIONS = 50;
  private static final int THREAD_POOL_SIZE = 10;
  private Map<ActionState, Map<ActionEvent, ActionState>> validTransitions = new HashMap<>();

  /** Setup the transitions */
  @BeforeClass
  public void setup() {
    final Map<ActionEvent, ActionState> submittedTransitions = new HashMap<>();
    submittedTransitions.put(ActionEvent.REQUEST_DISTRIBUTED, ActionState.PENDING);
    submittedTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionState.COMPLETED);
    submittedTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionState.ABORTED);
    validTransitions.put(ActionState.SUBMITTED, submittedTransitions);

    final Map<ActionEvent, ActionState> pendingTransitions = new HashMap<>();
    pendingTransitions.put(ActionEvent.REQUEST_FAILED, ActionState.SUBMITTED);
    pendingTransitions.put(ActionEvent.REQUEST_ACCEPTED, ActionState.ACTIVE);
    pendingTransitions.put(ActionEvent.REQUEST_DECLINED, ActionState.DECLINED);
    pendingTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionState.COMPLETED);
    pendingTransitions.put(ActionEvent.REQUEST_COMPLETED_DEACTIVATE, ActionState.COMPLETED);
    pendingTransitions.put(ActionEvent.REQUEST_COMPLETED_DISABLE, ActionState.COMPLETED);
    validTransitions.put(ActionState.PENDING, pendingTransitions);

    final Map<ActionEvent, ActionState> activeTransitions = new HashMap<>();
    activeTransitions.put(ActionEvent.REQUEST_FAILED, ActionState.SUBMITTED);
    activeTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionState.COMPLETED);
    activeTransitions.put(ActionEvent.REQUEST_COMPLETED_DEACTIVATE, ActionState.COMPLETED);
    activeTransitions.put(ActionEvent.REQUEST_COMPLETED_DISABLE, ActionState.COMPLETED);
    validTransitions.put(ActionState.ACTIVE, activeTransitions);

    final Map<ActionEvent, ActionState> completedTransitions = new HashMap<>();
    completedTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionState.COMPLETED);
    validTransitions.put(ActionState.COMPLETED, completedTransitions);

    final Map<ActionEvent, ActionState> reRunTransitions = new HashMap<>();
    reRunTransitions.put(ActionEvent.REQUEST_RERUN, ActionState.SUBMITTED);
    validTransitions.put(ActionState.ABORTED, reRunTransitions);
  }

  /** test a valid transition */
  @Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATIONS, timeOut = TIMEOUT)
  public void testActionTransitions() {
    final StateTransitionManagerFactory stmFactory = new ActionSvcStateTransitionManagerFactory();
    final StateTransitionManager<ActionState, ActionEvent> stm =
        stmFactory.getStateTransitionManager(ActionSvcStateTransitionManagerFactory.ACTION_ENTITY);

    validTransitions.forEach(
        (sourceState, transitions) -> {
          transitions.forEach(
              (actionEvent, actionState) -> {
                try {
                  Assert.assertEquals(actionState, stm.transition(sourceState, actionEvent));
                } catch (final CTPException re) {
                  Assert.fail("bad transition!", re);
                }
              });

          Arrays.asList(ActionEvent.values())
              .forEach(
                  event -> {
                    if (!transitions.keySet().contains(event)) {
                      boolean caught = false;
                      try {
                        stm.transition(sourceState, event);
                      } catch (final CTPException re) {
                        caught = true;
                      }
                      Assert.assertTrue(
                          caught,
                          "Transition " + sourceState + "(" + event + ") should be invalid");
                    }
                  });
        });
  }
}
