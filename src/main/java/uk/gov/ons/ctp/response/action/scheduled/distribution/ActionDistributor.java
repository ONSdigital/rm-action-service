package uk.gov.ons.ctp.response.action.scheduled.distribution;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

/**
 * This is the 'service' class that distributes actions to downstream services, ie services outside
 * of Response Management (ActionExporterSvc, NotifyGW, etc.).
 *
 * <p>This class has a self scheduled method wakeUp(), which looks for Actions in SUBMITTED state to
 * send to downstream handlers. On each wake cycle, it fetches the first n actions of each type, by
 * createddatatime, and forwards them to ActionProcessingService.
 */
@Component
@Slf4j
class ActionDistributor {

  @Autowired private AppConfig appConfig;

  @Autowired private ActionRepository actionRepo;

  @Autowired private ActionTypeRepository actionTypeRepo;

  @Autowired
  @Qualifier("business")
  private ActionProcessingService businessActionProcessingService;

  @Autowired
  @Qualifier("social")
  private ActionProcessingService socialActionProcessingService;

  @Autowired private ActionCaseRepository actionCaseRepo;

  /**
   * wake up on schedule and check for submitted actions, enrich and distribute them to spring
   * integration channels
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
    final InstructionCount requestCount =
        InstructionCount.builder()
            .actionTypeName(actionType.getName())
            .instruction(DistributionInfo.Instruction.REQUEST)
            .count(0)
            .build();
    final InstructionCount cancelCount =
        InstructionCount.builder()
            .actionTypeName(actionType.getName())
            .instruction(DistributionInfo.Instruction.CANCEL_REQUEST)
            .count(0)
            .build();

    try {
      final List<Action> actions = retrieveActions(actionType);
      processActions(actions, requestCount, cancelCount);
    } catch (final Exception e) {
      log.error("Failed to process action type {}", actionType, e);
    }
    return Arrays.asList(requestCount, cancelCount);
  }

  private void processActions(
      final List<Action> actions,
      final InstructionCount requestCount,
      final InstructionCount cancelCount) {
    log.debug(
        "Dealing with actions {}",
        actions.stream().map(Objects::toString).collect(Collectors.joining(",")));
    for (final Action action : actions) {
      try {
        processAction(action, requestCount, cancelCount);
      } catch (final Exception e) {
        log.error(
            "Could not processing action {}."
                + " Processing will be retried at next scheduled distribution",
            action.getId(),
            e);
      }
    }
  }

  public ActionProcessingService getActionProcessingService(Action action) {
    ActionCase acase = actionCaseRepo.findById(action.getCaseId());
    SampleUnitDTO.SampleUnitType caseType =
        SampleUnitDTO.SampleUnitType.valueOf(acase.getSampleUnitType());

    switch (caseType) {
      case H:
      case HI:
        return socialActionProcessingService;

      case B:
      case BI:
        return businessActionProcessingService;

      default:
        throw new UnsupportedOperationException("Sample Type: " + caseType + " is not supported!");
    }
  }

  private void processAction(
      Action action, final InstructionCount requestCount, final InstructionCount cancelCount)
      throws CTPException {
    ActionProcessingService ap = getActionProcessingService(action);

    if (action.getState().equals(ActionState.SUBMITTED)) {
      ap.processActionRequest(action);
      requestCount.increment();
    } else if (action.getState().equals(ActionState.CANCEL_SUBMITTED)) {
      ap.processActionCancel(action);
      cancelCount.increment();
    }
  }

  /**
   * Get the oldest page of submitted actions by type
   *
   * @param actionType the type
   * @return list of actions
   */
  private List<Action> retrieveActions(final ActionType actionType) {
    List<Action> actions =
        actionRepo.findSubmittedOrCancelledByActionTypeName(
            actionType.getName(), appConfig.getActionDistribution().getRetrievalMax());
    log.debug(
        "RETRIEVED action ids {}",
        actions.stream().map(a -> a.getActionPK().toString()).collect(Collectors.joining(",")));
    return actions;
  }
}
