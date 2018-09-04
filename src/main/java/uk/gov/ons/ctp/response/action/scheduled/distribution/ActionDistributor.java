package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

  private static final String lockPrefix = "ActionDistributionLock-";

  private AppConfig appConfig;
  private RedissonClient redissonClient;

  private ActionRepository actionRepo;
  private ActionCaseRepository actionCaseRepo;
  private ActionTypeRepository actionTypeRepo;

  private ActionProcessingService businessActionProcessingService;
  private ActionProcessingService socialActionProcessingService;

  public ActionDistributor(
      AppConfig appConfig,
      RedissonClient redissonClient,
      ActionRepository actionRepo,
      ActionCaseRepository actionCaseRepo,
      ActionTypeRepository actionTypeRepo,
      @Qualifier("business") ActionProcessingService businessActionProcessingService,
      @Qualifier("social") ActionProcessingService socialActionProcessingService) {
    this.appConfig = appConfig;
    this.redissonClient = redissonClient;
    this.actionRepo = actionRepo;
    this.actionCaseRepo = actionCaseRepo;
    this.actionTypeRepo = actionTypeRepo;
    this.businessActionProcessingService = businessActionProcessingService;
    this.socialActionProcessingService = socialActionProcessingService;
  }

  /**
   * Called on schedule to check for submitted actions then creates and distributes the actions to
   * action exporter or notify gateway
   */
  @Transactional
  public void distribute() {
    List<ActionType> actionTypes = actionTypeRepo.findAll();
    actionTypes.forEach(this::processActionType);
  }

  private void processActionType(final ActionType actionType) {
    String actionTypeName = actionType.getName();
    RLock lock = redissonClient.getFairLock(lockPrefix + actionTypeName);
    try {
      // Wait for a lock. Automatically unlock after a certain amount of time to prevent issues
      // when lock holder crashes or Redis crashes causing permanent lockout
      if (lock.tryLock(
          appConfig.getDataGrid().getLockTimeToWaitSeconds(),
          appConfig.getDataGrid().getLockTimeToLiveSeconds(),
          TimeUnit.SECONDS)) {
        try {
          List<Action> actions =
              actionRepo.findSubmittedOrCancelledByActionTypeName(
                  actionType.getName(), appConfig.getActionDistribution().getRetrievalMax());
          actions.forEach(this::processAction);
        } finally {
          // Always unlock the distributed lock
          lock.unlock();
        }
      }
    } catch (InterruptedException ex) {
      // Ignored - process stopped while waiting for lock
    }
  }

  private void processAction(Action action) {
    ActionProcessingService ap = getActionProcessingService(action);

    try {
      if (action.getState().equals(ActionState.SUBMITTED)) {
        ap.processActionRequests(action);
      } else if (action.getState().equals(ActionState.CANCEL_SUBMITTED)) {
        ap.processActionCancel(action);
      }
    } catch (CTPException ex) {
      log.with("action_id", action.getId().toString())
          .error("Failed to transition action. Will be retried at next schedule");
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
}
