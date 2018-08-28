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
 * Service for receiving notifications from case service Creates/Updates/Deletes cases in the
 * action.case table
 */
@Service
public class CaseNotificationService {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationService.class);

  private static final int TRANSACTION_TIMEOUT = 30;

  private final ActionCaseRepository actionCaseRepo;
  private final ActionPlanRepository actionPlanRepo;

  private final ActionService actionService;

  private final CollectionExerciseClientService collectionSvcClientService;

  public CaseNotificationService(
      ActionCaseRepository actionCaseRepo,
      ActionPlanRepository actionPlanRepo,
      ActionService actionService,
      CollectionExerciseClientService collectionSvcClientService) {
    this.actionCaseRepo = actionCaseRepo;
    this.actionPlanRepo = actionPlanRepo;
    this.actionService = actionService;
    this.collectionSvcClientService = collectionSvcClientService;
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public void acceptNotification(final CaseNotification notification) throws CTPException {

    UUID caseId = UUID.fromString(notification.getCaseId());

    switch (notification.getNotificationType()) {
      case REPLACED:
      case ACTIVATED:
        ActionCase actionCase = createActionCase(notification);
        saveActionCase(actionCase);
        break;

      case ACTIONPLAN_CHANGED:
        updateActionCase(caseId, UUID.fromString(notification.getActionPlanId()));
        break;

      case DISABLED:
      case DEACTIVATED:
        actionService.cancelActions(caseId);
        deleteActionCase(caseId);
        break;

      default:
        log.with("notification_type", notification.getNotificationType())
            .warn("Unknown case notification type", notification.getNotificationType());
        throw new IllegalArgumentException("Invalid notification type");
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

    CollectionExerciseDTO collectionExercise =
        collectionSvcClientService.getCollectionExercise(collectionExerciseId);
    Timestamp startDateTime =
        new Timestamp(collectionExercise.getScheduledStartDateTime().getTime());
    Timestamp endDateTime = new Timestamp(collectionExercise.getScheduledEndDateTime().getTime());
    return ActionCase.builder()
        .id(caseId)
        .sampleUnitId(sampleUnitId)
        .actionPlanId(actionPlanId)
        .actionPlanFK(actionPlan.getActionPlanPK())
        .collectionExerciseId(collectionExerciseId)
        .actionPlanStartDate(startDateTime)
        .actionPlanEndDate(endDateTime)
        .partyId(partyId)
        .sampleUnitType(notification.getSampleUnitType())
        .build();
  }

  private void saveActionCase(ActionCase actionCase) throws CTPException {
    if (actionCaseRepo.findById(actionCase.getId()) != null) {
      log.with("case_id", actionCase.getId().toString())
          .error("Can't create case as it already exists");
      throw new CTPException(CTPException.Fault.RESOURCE_VERSION_CONFLICT);
    }
    actionCaseRepo.save(actionCase);
  }

  private void updateActionCase(UUID actionCaseId, UUID actionPlanId) throws CTPException {
    ActionCase existingCase = actionCaseRepo.findById(actionCaseId);
    if (existingCase == null) {
      log.with("case_id", actionCaseId.toString()).error("No case found to update");
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND);
    }
    ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, actionPlanId.toString());
    }
    existingCase.setActionPlanFK(actionPlan.getActionPlanPK());
    existingCase.setActionPlanId(actionPlanId);
    log.with("case_id", actionCaseId.toString())
        .with("action_plan_id", actionPlanId.toString())
        .info("Updating case");
    actionCaseRepo.save(existingCase);
  }

  private void deleteActionCase(UUID actionCaseId) throws CTPException {
    ActionCase actionCaseToDelete = actionCaseRepo.findById(actionCaseId);
    if (actionCaseToDelete == null) {
      log.with("case_id", actionCaseId.toString()).error("No case found to delete");
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND);
    }
    log.with("case_id", actionCaseId.toString()).info("Deleting case");
    actionCaseRepo.delete(actionCaseToDelete);
  }
}
