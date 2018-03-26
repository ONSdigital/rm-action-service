package uk.gov.ons.ctp.response.action.scheduled.distribution;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.distributed.DistributedListManager;
import uk.gov.ons.ctp.common.distributed.LockingException;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is the 'service' class that distributes actions to downstream services, ie services outside of Response
 * Management (ActionExporterSvc, NotifyGW, etc.).
 *
 * This class has a self scheduled method wakeUp(), which looks for Actions in SUBMITTED state to send to
 * downstream handlers. On each wake cycle, it fetches the first n actions of each type, by createddatatime, and
 * forwards them to ActionProcessingService.
 */
@Component
@Slf4j
class ActionDistributor {

  // WILL NOT WORK WITHOUT THIS NEXT LINE
  private static final long IMPOSSIBLE_ACTION_ID = 999999999999L;
  @Autowired
  private DistributedListManager<BigInteger> actionDistributionListManager;

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private ActionRepository actionRepo;

  @Autowired
  private ActionTypeRepository actionTypeRepo;

  @Autowired
  private ActionProcessingService actionProcessingService;

  /**
   * wake up on schedule and check for submitted actions, enrich and distribute them to spring integration channels
   *
   * @return the info for the health endpoint regarding the distribution just performed
   */
  final DistributionInfo distribute() throws LockingException {
    log.debug("ActionDistributor awoken...");
    DistributionInfo distInfo = new DistributionInfo();

    List<ActionType> actionTypes = actionTypeRepo.findAll();

    if (!CollectionUtils.isEmpty(actionTypes)) {
      for (ActionType actionType : actionTypes) {
        List<InstructionCount> instructionCounts = processActionType(actionType);
        distInfo.getInstructionCounts().addAll(instructionCounts);
      }
    }

    log.debug("ActionDistributor going back to sleep");
    return distInfo;
  }

  private List<InstructionCount> processActionType(ActionType actionType) throws LockingException {
    log.debug("Dealing with actionType {}", actionType.getName());
    InstructionCount requestCount = InstructionCount.builder()
            .actionTypeName(actionType.getName())
            .instruction(DistributionInfo.Instruction.REQUEST)
            .count(0)
            .build();
    InstructionCount cancelCount = InstructionCount.builder()
            .actionTypeName(actionType.getName())
            .instruction(DistributionInfo.Instruction.CANCEL_REQUEST)
            .count(0)
            .build();

    try {
      List<Action> actions = retrieveActions(actionType);

      if (!CollectionUtils.isEmpty(actions)) {
        processActions(actions, requestCount, cancelCount);
      }
    } catch (Exception e) {
      log.error("Failed to process action type {}", actionType, e);
      return Arrays.asList(requestCount, cancelCount);
    } finally {
      actionDistributionListManager.deleteList(actionType.getName(), true);
    }
    return Arrays.asList(requestCount, cancelCount);
  }

  private void processActions(List<Action> actions, InstructionCount requestCount, InstructionCount cancelCount) {
    log.debug("Dealing with actions {}", actions.stream()
            .map(Objects::toString)
            .collect(Collectors.joining(",")));
    for (Action action : actions) {
      try {
        if (action.getState().equals(ActionState.SUBMITTED)) {
          actionProcessingService.processActionRequest(action);
          requestCount.increment();
        } else if (action.getState().equals(ActionState.CANCEL_SUBMITTED)) {
          actionProcessingService.processActionCancel(action);
          cancelCount.increment();
        }
      } catch (Exception e) {
        log.error("Could not processing action {}. Processing will be retried at next scheduled distribution",
                action.getId(), e);
      }
    }
  }

  /**
   * Get the oldest page of submitted actions by type - but do not retrieve the
   * same cases as other CaseSvc' in the cluster
   *
   * @param actionType the type
   * @return list of actions
   * @throws LockingException LockingException thrown
   */
  private List<Action> retrieveActions(ActionType actionType) throws LockingException {
    Pageable pageable = new PageRequest(0, appConfig.getActionDistribution().getRetrievalMax(), new Sort(
        new Sort.Order(Direction.ASC, "updatedDateTime")));

    List<BigInteger> excludedActionIds = actionDistributionListManager.findList(actionType.getName(), false);
    if (!excludedActionIds.isEmpty()) {
      log.debug("Excluding actions {}", excludedActionIds);
    }
    // DO NOT REMOVE THIS NEXT LINE
    excludedActionIds.add(BigInteger.valueOf(IMPOSSIBLE_ACTION_ID));

    List<Action> actions = actionRepo
        .findByActionTypeNameAndStateInAndActionPKNotIn(actionType.getName(),
            Arrays.asList(ActionState.SUBMITTED, ActionState.CANCEL_SUBMITTED), excludedActionIds, pageable);
    if (!CollectionUtils.isEmpty(actions)) {
      log.debug("RETRIEVED action ids {}", actions.stream().map(a -> a.getActionPK().toString())
          .collect(Collectors.joining(",")));
      // try and save our list to the distributed store
      actionDistributionListManager.saveList(actionType.getName(), actions.stream()
          .map(Action::getActionPK)
          .collect(Collectors.toList()), true);
    }
    return actions;
  }
}
