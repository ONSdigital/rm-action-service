package uk.gov.ons.ctp.response.action.scheduled.distribution;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is the 'service' class that distributes actions to downstream services, ie services outside of Response
 * Management (ActionExporterSvc, NotifyGW, etc.).
 * <p>
 * This class has a self scheduled method wakeUp(), which looks for Actions in SUBMITTED state to send to
 * downstream handlers. On each wake cycle, it fetches the first n actions of each type, by createddatatime, and
 * forwards them to ActionProcessingService.
 */
@Component
@Slf4j
class ActionDistributor {

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
  @Transactional
  public DistributionInfo distribute() {
    log.debug("ActionDistributor awoken...");
    final DistributionInfo distInfo = new DistributionInfo();

    final List<ActionType> actionTypes = actionTypeRepo.findAll();

    if (!CollectionUtils.isEmpty(actionTypes)) {
      for (final ActionType actionType : actionTypes) {
        final List<InstructionCount> instructionCounts = processActionType(actionType);
        distInfo.getInstructionCounts().addAll(instructionCounts);
      }
    }

    log.debug("ActionDistributor going back to sleep");
    return distInfo;
  }

  private List<InstructionCount> processActionType(final ActionType actionType) {
    log.debug("Dealing with actionType {}", actionType.getName());
    final InstructionCount requestCount = InstructionCount.builder()
        .actionTypeName(actionType.getName())
        .instruction(DistributionInfo.Instruction.REQUEST)
        .count(0)
        .build();
    final InstructionCount cancelCount = InstructionCount.builder()
        .actionTypeName(actionType.getName())
        .instruction(DistributionInfo.Instruction.CANCEL_REQUEST)
        .count(0)
        .build();

    try {
      final List<Action> actions = retrieveActions(actionType);

      if (!CollectionUtils.isEmpty(actions)) {
        processActions(actions, requestCount, cancelCount);
      }
    } catch (final Exception e) {
      log.error("Failed to process action type {}", actionType, e);
    }
    return Arrays.asList(requestCount, cancelCount);
  }

  private void processActions(final List<Action> actions, final InstructionCount requestCount, final InstructionCount cancelCount) {
    log.debug("Dealing with actions {}", actions.stream()
        .map(Objects::toString)
        .collect(Collectors.joining(",")));
    for (final Action action : actions) {
      try {
        if (action.getState().equals(ActionState.SUBMITTED)) {
          actionProcessingService.processActionRequest(action);
          requestCount.increment();
        } else if (action.getState().equals(ActionState.CANCEL_SUBMITTED)) {
          actionProcessingService.processActionCancel(action);
          cancelCount.increment();
        }
      } catch (final Exception e) {
        log.error("Could not processing action {}. Processing will be retried at next scheduled distribution",
            action.getId(), e);
      }
    }
  }

  /**
   * Get the oldest page of submitted actions by type
   *
   * @param actionType the type
   * @return list of actions
   */
  private List<Action> retrieveActions(final ActionType actionType) {
    List<Action> actions = actionRepo.findSubmittedOrCancelledByActionTypeName(actionType.getName(),
            appConfig.getActionDistribution().getRetrievalMax());
    if (CollectionUtils.isNotEmpty(actions)) {
      log.debug("RETRIEVED action ids {}", actions.stream().map(a -> a.getActionPK().toString())
          .collect(Collectors.joining(",")));
    }
    return actions;
  }
}
