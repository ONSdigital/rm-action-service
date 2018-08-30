package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Arrays;
import java.util.List;
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

/** This is the service class that distributes actions to downstream services */
@Component
class ActionDistributor {
  private static final Logger log = LoggerFactory.getLogger(ActionDistributor.class);

  private final AppConfig appConfig;

  private ActionRepository actionRepo;
  private ActionCaseRepository actionCaseRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService businessActionProcessingService;
  private ActionProcessingService socialActionProcessingService;

  public ActionDistributor(
      AppConfig appConfig,
      ActionRepository actionRepo,
      ActionCaseRepository actionCaseRepo,
      ActionTypeRepository actionTypeRepo,
      @Qualifier("business") ActionProcessingService businessActionProcessingService,
      @Qualifier("social") ActionProcessingService socialActionProcessingService) {
    this.appConfig = appConfig;
    this.actionRepo = actionRepo;
    this.actionCaseRepo = actionCaseRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.businessActionProcessingService = businessActionProcessingService;
    this.socialActionProcessingService = socialActionProcessingService;
  }

  /**
   * Called on schedule to check for submitted actions then creates and distributes the actions to
   * action exporter or notify gateway
   *
   * @return the info for the health endpoint regarding the distribution just performed
   */
  @Transactional
  public DistributionInfo distribute() {
    final DistributionInfo distInfo = new DistributionInfo();
    List<ActionType> actionTypes = actionTypeRepo.findAll();

    for (final ActionType actionType : actionTypes) {
      final List<InstructionCount> instructionCounts = processActionType(actionType);
      distInfo.getInstructionCounts().addAll(instructionCounts);
    }
    return distInfo;
  }

  private List<InstructionCount> processActionType(final ActionType actionType) {
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
      List<Action> actions = retrieveActions(actionType);
      processActions(actions, requestCount, cancelCount);
    } catch (Exception e) {
      log.with("action_type", actionType).error("Failed to process", e);
    }
    return Arrays.asList(requestCount, cancelCount);
  }

  private List<Action> retrieveActions(final ActionType actionType) {
    return actionRepo.findSubmittedOrCancelledByActionTypeName(
        actionType.getName(), appConfig.getActionDistribution().getRetrievalMax());
  }

  private void processActions(
      final List<Action> actions,
      final InstructionCount requestCount,
      final InstructionCount cancelCount) {
    for (Action action : actions) {
      try {
        processAction(action, requestCount, cancelCount);
      } catch (Exception e) {
        log.with("action_id", action.getId())
            .error("Could not process action. Will be retried at next schedule", e);
      }
    }
  }

  private void processAction(
      Action action, InstructionCount requestCount, InstructionCount cancelCount)
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

  private ActionProcessingService getActionProcessingService(Action action) {
    ActionCase acase = actionCaseRepo.findById(action.getCaseId());
    SampleUnitDTO.SampleUnitType caseType =
        SampleUnitDTO.SampleUnitType.valueOf(acase.getSampleUnitType());

    switch (caseType) {
      case H:
      case HI:
        return socialActionProcessingService;

      case B:
        return businessActionProcessingService;

      default:
        throw new UnsupportedOperationException("Sample Type: " + caseType + " is not supported!");
    }
  }
}
