package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
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
class ActionDistributor {

  private static final Logger log = LoggerFactory.getLogger(ActionDistributor.class);

  private static final String SOCIALREM = "SOCIALREM";

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

  @Autowired private CaseSvcClientService caseSvcClientService;

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

    for (final ActionType actionType : actionTypes) {
      final List<InstructionCount> instructionCounts = processActionType(actionType);
      distInfo.getInstructionCounts().addAll(instructionCounts);
    }

    log.debug("ActionDistributor going back to sleep");
    return distInfo;
  }

  private List<InstructionCount> processActionType(final ActionType actionType) {
    log.with("action_type", actionType.getName()).debug("Dealing with actionType");
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
      log.with("action_type", actionType.getName()).error("Failed to process action type", e);
    }
    return Arrays.asList(requestCount, cancelCount);
  }

  private void processActions(
      final List<Action> actions,
      final InstructionCount requestCount,
      final InstructionCount cancelCount) {
    log.with(actions).debug("Dealing with actions");
    for (final Action action : actions) {
      try {
        processAction(action, requestCount, cancelCount);
      } catch (final Exception e) {
        log.with("action_id", action.getId())
            .error("Could not process action, will be retried at next scheduled distribution", e);
      }
    }
  }

  private ActionProcessingService getActionProcessingService(Action action) {
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

    if (action.getActionType().getName().equals(SOCIALREM)
        && action.getActionType().getActionTypeNameEnum()
            == uk.gov.ons.ctp.response.action.representation.ActionType.SOCIALREM) {
      caseSvcClientService.generateNewIacForCase(action.getCaseId());
    }

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
    String actionIds =
        actions.stream().map(a -> a.getActionPK().toString()).collect(Collectors.joining(","));
    log.with("action_type", actionType.getName())
        .with("action_ids", actionIds)
        .debug("RETRIEVED action ids");
    return actions;
  }
}
