package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/**
 * Service for receiving notifications from case service
 * Creates/Updates/Deletes cases in the action.case table
 */
@Service
public class CaseNotificationService {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationService.class);

  private static final int TRANSACTION_TIMEOUT = 30;

  private ActionCaseRepository actionCaseRepo;

  private ActionPlanRepository actionPlanRepo;

  private ActionService actionService;

  private CollectionExerciseClientService collectionSvcClientServiceImpl;

  public CaseNotificationService(
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepo,
      ActionService actionService,
      CollectionExerciseClientService collectionSvcClientServiceImpl) {
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepo = actionPlanRepo;
    this.actionService = actionService;
    this.collectionSvcClientServiceImpl = collectionSvcClientServiceImpl;
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public void acceptNotification(final CaseNotification notification) throws CTPException {

    ActionCase actionCase = createActionCase(notification);
    UUID caseId = actionCase.getId();

    switch (notification.getNotificationType()) {
      case ACTIONPLAN_CHANGED:
        updateActionCase(actionCase);
        break;

      case REPLACED:
      case ACTIVATED:
        createActionCase(actionCase);
        break;

      case DISABLED:
      case DEACTIVATED:
        actionService.cancelActions(caseId);
        deleteActionCase(actionCase);
        break;

      default:
        log.with("notificationType", notification.getNotificationType())
          .warn("Unknown case notification type", notification.getNotificationType());
        break;
    }
    actionCaseRepo.flush();
  }

  private ActionCase createActionCase(CaseNotification notification) throws CTPException {
    UUID actionPlanId = UUID.fromString(notification.getActionPlanId());
    ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, actionPlanId.toString());
    }
    UUID caseId = UUID.fromString(notification.getCaseId());
    UUID collectionExerciseId = UUID.fromString(notification.getExerciseId());
    UUID partyId =
        notification.getPartyId() == null ? null : UUID.fromString(notification.getPartyId());
    UUID sampleUnitId =
        notification.getSampleUnitId() == null
            ? null
            : UUID.fromString(notification.getSampleUnitId());

    return ActionCase.builder()
        .id(caseId)
        .sampleUnitId(sampleUnitId)
        .actionPlanId(actionPlanId)
        .actionPlanFK(actionPlan.getActionPlanPK())
        .collectionExerciseId(collectionExerciseId)
        .partyId(partyId)
        .sampleUnitType(notification.getSampleUnitType())
        .build();
  }

  private void createActionCase(ActionCase actionCase) throws CTPException {
    if (actionCaseRepo.findById(actionCase.getId()) != null) {
      log.with("caseId", actionCase.getId().toString())
          .error("Can't create case as it already exists");
      throw new CTPException(CTPException.Fault.RESOURCE_VERSION_CONFLICT);
    }
    final CollectionExerciseDTO collectionExercise =
        collectionSvcClientServiceImpl.getCollectionExercise(actionCase.getCollectionExerciseId());
    actionCase.setActionPlanStartDate(
        new Timestamp(collectionExercise.getScheduledStartDateTime().getTime()));
    actionCase.setActionPlanEndDate(
        new Timestamp(collectionExercise.getScheduledEndDateTime().getTime()));
    actionCaseRepo.save(actionCase);
  }

  private void updateActionCase(ActionCase actionCase) throws CTPException {
    ActionCase existingCase = actionCaseRepo.findById(actionCase.getId());
    if (existingCase == null) {
      log.with("caseId", actionCase.getId().toString()).error("No case found to update");
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND);
    }
    log.with("caseId", actionCase.getId().toString())
        .with("actionPlanId", actionCase.getActionPlanId().toString())
        .info("Updating case");
    actionCaseRepo.save(actionCase);
  }

  private void deleteActionCase(ActionCase actionCase) throws CTPException {
    ActionCase actionCaseToDelete = actionCaseRepo.findById(actionCase.getId());
    if (actionCaseToDelete == null) {
      log.with("caseId", actionCase.getId().toString()).error("No case found to delete");
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND);
    }
    log.with("caseId", actionCase.getId().toString()).info("Deleting case");
    actionCaseRepo.delete(actionCaseToDelete);
  }
}
