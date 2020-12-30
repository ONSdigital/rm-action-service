package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/**
 * Service for receiving notifications from case service. Creates/Updates/Deletes cases in the
 * action.case table.
 */
@Service
public class CaseNotificationService {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationService.class);

  private static final String ACTION_PLAN_NOT_FOUND = "No action plan found with id: %s";
  private static final String ACTION_CASE_ALREADY_EXISTS = "Action case already exists with id: %s";
  private static final String ACTION_CASE_NOT_FOUND = "No action case found with id: %s";
  private static final String INVALID_NOTIFICATION_TYPE =
      "Invalid notification type, notification_type=%s";
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
    UUID actionPlanId =
        notification.getActionPlanId() != null
            ? UUID.fromString(notification.getActionPlanId())
            : null;

    switch (notification.getNotificationType()) {
      case REPLACED:
      case ACTIVATED:
        ActionCase actionCase = createActionCase(notification);
        saveActionCase(actionCase);
        break;

      case ACTIONPLAN_CHANGED:
        updateActionCase(caseId, actionPlanId, notification);
        break;

      case DISABLED:
      case DEACTIVATED:
        actionService.cancelActions(caseId);
        deleteActionCase(caseId);
        break;

      default:
        throw new IllegalArgumentException(
            String.format(
                INVALID_NOTIFICATION_TYPE, notification.getNotificationType().toString()));
    }
    actionCaseRepo.flush();
  }

  private ActionCase createActionCase(CaseNotification notification) throws NullPointerException {
    // To Do - remove this code once action plan is deprecated
    if (notification.getActionPlanId() != null) {
      UUID actionPlanId = UUID.fromString(notification.getActionPlanId());
      ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
      if (actionPlan == null) {
        throw new IllegalStateException(
            String.format(ACTION_PLAN_NOT_FOUND, actionPlanId.toString()));
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
          .activeEnrolment(notification.isActiveEnrolment())
          .actionPlanFK(actionPlan.getActionPlanPK())
          .collectionExerciseId(collectionExerciseId)
          .actionPlanStartDate(startDateTime)
          .actionPlanEndDate(endDateTime)
          .partyId(partyId)
          .sampleUnitType(notification.getSampleUnitType())
          .build();
    } else {
      UUID caseId = UUID.fromString(notification.getCaseId());
      UUID collectionExerciseId = UUID.fromString(notification.getExerciseId());
      UUID partyId =
          notification.getPartyId() == null ? null : UUID.fromString(notification.getPartyId());
      UUID sampleUnitId =
          notification.getSampleUnitId() == null
              ? null
              : UUID.fromString(notification.getSampleUnitId());

      // CollectionExerciseDTO collectionExercise =
      // collectionSvcClientService.getCollectionExercise(collectionExerciseId);
      return ActionCase.builder()
          .id(caseId)
          .sampleUnitId(sampleUnitId)
          .activeEnrolment(notification.isActiveEnrolment())
          .collectionExerciseId(collectionExerciseId)
          .partyId(partyId)
          .sampleUnitType(notification.getSampleUnitType())
          .sampleUnitRef(notification.getSampleUnitRef())
          .status(notification.getStatus())
          .iac(notification.getIac())
          .build();
    }
  }

  private void saveActionCase(ActionCase actionCase) {
    if (actionCaseRepo.findById(actionCase.getId()) != null) {
      log.with("case_id", actionCase.getId().toString())
          .error("Can't create case as it already exists");
      throw new IllegalStateException(
          String.format(ACTION_CASE_ALREADY_EXISTS, actionCase.getId().toString()));
    }
    actionCaseRepo.save(actionCase);
  }

  private void updateActionCase(
      UUID actionCaseId, UUID actionPlanId, CaseNotification notification) {
    ActionCase existingCase = actionCaseRepo.findById(actionCaseId);
    if (existingCase == null) {
      log.with("case_id", actionCaseId.toString()).error("No case found to update");
      throw new IllegalStateException(
          String.format(ACTION_CASE_NOT_FOUND, actionCaseId.toString()));
    }
    // To Do - remove this code once action plan is deprecated
    if (actionPlanId != null) {
      ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
      if (actionPlan == null) {
        log.with("action_plan_id", actionPlanId.toString()).error("No action plan found");
        throw new IllegalStateException(
            String.format(ACTION_PLAN_NOT_FOUND, actionPlanId.toString()));
      }
      existingCase.setActionPlanFK(actionPlan.getActionPlanPK());
      existingCase.setActionPlanId(actionPlanId);
      log.with("case_id", actionCaseId.toString())
          .with("action_plan_id", actionPlanId.toString())
          .debug("Updating case action plan");
    } else {
      existingCase.setStatus(notification.getStatus());
      existingCase.setActiveEnrolment(notification.isActiveEnrolment());
      existingCase.setIac(notification.getIac());
    }
    actionCaseRepo.save(existingCase);
  }

  private void deleteActionCase(UUID actionCaseId) {
    ActionCase actionCaseToDelete = actionCaseRepo.findById(actionCaseId);
    if (actionCaseToDelete == null) {
      log.with("case_id", actionCaseId.toString()).warn("No case found to delete");
      return;
    }
    log.with("case_id", actionCaseId.toString()).info("Deleting case");
    actionCaseRepo.delete(actionCaseToDelete);
  }
}
