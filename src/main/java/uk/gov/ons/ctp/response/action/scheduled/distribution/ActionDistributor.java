package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;

/** This is the service class that distributes actions to downstream services */
@Component
public class ActionDistributor {

  private static final Logger log = LoggerFactory.getLogger(ActionDistributor.class);

  private static final int TRANSACTION_TIMEOUT_SECONDS = 3600;
  private static final Set<ActionState> ACTION_STATES_TO_GET =
      Sets.immutableEnumSet(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED);

  private ActionRepository actionRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService businessActionProcessingService;

  public ActionDistributor(
      ActionRepository actionRepo,
      ActionTypeRepository actionTypeRepo,
      ActionProcessingService businessActionProcessingService) {
    this.actionRepo = actionRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.businessActionProcessingService = businessActionProcessingService;
  }

  /**
   * Called on schedule to check for submitted actions then creates and distributes requests to
   * action exporter or notify gateway
   */
  @Transactional(timeout = TRANSACTION_TIMEOUT_SECONDS)
  public void distribute() {
    List<ActionType> actionTypes = actionTypeRepo.findAll();
    actionTypes.forEach(this::processActionType);
  }

  private void processActionType(final ActionType actionType) {
    log.with("type", actionType.getName()).trace("Processing actionType");
    Stream<Action> stream = actionRepo.findByActionTypeAndStateIn(actionType, ACTION_STATES_TO_GET);
    List<Action> allActions = stream.collect(Collectors.toList());

    businessActionProcessingService.processActions(actionType, allActions);
  }
}
