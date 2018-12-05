package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.Sets;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;

/**
 * This bean will have the actionDistributor injected into it by spring on constructions. It will
 * then schedule the running of the distributor using details from the AppConfig
 */
@CoverageIgnore
@Component
public class DistributionScheduler {
  private static final Logger log = LoggerFactory.getLogger(DistributionScheduler.class);

  @Autowired private ActionDistributor actionDistributor;
  @Autowired private ActionRepository actionRepo;
  @Autowired private ActionTypeRepository actionTypeRepo;

  private static final Set<ActionState> ACTION_STATES_TO_GET =
      Sets.immutableEnumSet(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED);

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(50);
  private static final AtomicInteger actionsDistributed = new AtomicInteger();

  /** Scheduled execution of the Action Distributor */
  @Scheduled(fixedDelayString = "#{appConfig.actionDistribution.delayMilliSeconds}")
  //  @Transactional(readOnly = true)
  public void run() {
    try {
      actionDistributor.distribute();
    } catch (Exception ex) {
      log.error("Uncaught exception", ex);
    }
  }

  private void distribute() {
    List<ActionType> actionTypes = actionTypeRepo.findAll();
    actionTypes.forEach(this::processActionType);
  }

  private void processActionType(final ActionType actionType) {
    List<Callable<Boolean>> callables = new LinkedList<>();

    try (Stream<Action> actions =
        actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET)) {

      actions.forEach(
          action -> {
            callables.add(
                () -> {
                  if (actionsDistributed.incrementAndGet() % 500 == 0) {
                    log.info("Distributed {} actions", actionsDistributed.get());
                  }
                  actionDistributor.processAction(action);
                  return Boolean.TRUE;
                });
          });

      try {
        EXECUTOR_SERVICE.invokeAll(callables);
      } catch (InterruptedException e) {
        log.error(
            "THIS IS ALSO THE WORST THING TO EVER HAPPEN IN THE ENTIRE HISTORY OF EVERYTHING "
                + "EVER",
            e);
      }

      //      actionRepo.flush();
    }
  }
}
