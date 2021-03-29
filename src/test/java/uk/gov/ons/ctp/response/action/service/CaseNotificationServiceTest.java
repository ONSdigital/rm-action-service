package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;

/** Tests for the CaseNotificationServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class CaseNotificationServiceTest {

  private static final String DUMMY_UUID = "7bc5d41b-0549-40b3-ba76-42f6d4cf3991";
  @Mock private ActionCaseRepository actionCaseRepo;
  @Mock private ActionPlanRepository actionPlanRepo;
  @Mock private CollectionExerciseClientService collectionSvcClientServiceImpl;

  @InjectMocks private CaseNotificationService caseNotificationService;

  private ActionPlan actionPlan;
  private ActionCase actionCase;
  private CaseNotification caseNotification;
  private List<CollectionExerciseDTO> collectionExercises;

  @Before
  public void setUp() throws Exception {
    actionPlan = new ActionPlan();
    actionPlan.setActionPlanPK(1);
    actionCase = new ActionCase();
    collectionExercises = FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
  }

  private CaseNotification createCaseNotification(
      NotificationType notificationType, Boolean activeEnrolment) {
    final CaseNotification caseNotification = new CaseNotification();
    caseNotification.setCaseId(DUMMY_UUID);
    caseNotification.setNotificationType(notificationType);
    caseNotification.setExerciseId(DUMMY_UUID);
    caseNotification.setPartyId(DUMMY_UUID);
    caseNotification.setSampleUnitId(DUMMY_UUID);
    if (activeEnrolment != null) {
      caseNotification.setActiveEnrolment(activeEnrolment);
    }
    return caseNotification;
  }

  @Test
  public void testAcceptNotificationActiveEnrolmentTrue() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(collectionSvcClientServiceImpl.getCollectionExercise(UUID.fromString(DUMMY_UUID)))
        .thenReturn(collectionExercises.get(0));

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIVATED, true);
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).save(actionCase.capture());
    final List<ActionCase> caze = actionCase.getAllValues();

    verify(actionCaseRepo, times(1)).flush();
    assertTrue(caze.get(0).isActiveEnrolment());
    assertEquals(UUID.fromString(DUMMY_UUID), caze.get(0).getId());
  }

  @Test
  public void testAcceptNotificationActiveEnrolmentFalse() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(collectionSvcClientServiceImpl.getCollectionExercise(UUID.fromString(DUMMY_UUID)))
        .thenReturn(collectionExercises.get(0));

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIVATED, false);
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).save(actionCase.capture());
    final List<ActionCase> caze = actionCase.getAllValues();

    verify(actionCaseRepo, times(1)).flush();
    assertFalse(caze.get(0).isActiveEnrolment());
    assertEquals(UUID.fromString(DUMMY_UUID), caze.get(0).getId());
  }

  @Test
  public void testAcceptNotificationActiveEnrolmentWillDefaultedToFalse() throws Exception {

    // When
    when(actionPlanRepo.findById(any())).thenReturn(null);
    when(collectionSvcClientServiceImpl.getCollectionExercise(UUID.fromString(DUMMY_UUID)))
        .thenReturn(collectionExercises.get(0));
    caseNotification = createCaseNotification(NotificationType.ACTIVATED, false);
    caseNotificationService.acceptNotification(caseNotification);
    // Then throws an IllegalStateException
    // Then
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).save(actionCase.capture());
    final List<ActionCase> caze = actionCase.getAllValues();

    verify(actionCaseRepo, times(1)).flush();
    assertFalse(caze.get(0).isActiveEnrolment());
    assertEquals(UUID.fromString(DUMMY_UUID), caze.get(0).getId());
  }

  @Test(expected = IllegalStateException.class)
  public void testAcceptNotificationActionCaseAlreadyExists() throws Exception {

    // Given a action case for DUMMY_UUID already exists
    when(collectionSvcClientServiceImpl.getCollectionExercise(UUID.fromString(DUMMY_UUID)))
        .thenReturn(collectionExercises.get(0));
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIVATED, true);
    caseNotificationService.acceptNotification(caseNotification);

    // Then throws an IllegalStateException
  }

  @Test
  public void testAcceptNotificationUpdatesActiveEnrolment() throws Exception {

    // Given
    actionCase.setId(UUID.fromString(DUMMY_UUID));
    actionCase.setActiveEnrolment(false);
    actionCase.setSampleUnitType(DUMMY_UUID);
    actionCase.setIac("40012578");
    actionCase.setSampleUnitRef(DUMMY_UUID);
    actionCase.setSampleUnitId(UUID.fromString(DUMMY_UUID));
    actionCase.setPartyId(UUID.fromString(DUMMY_UUID));

    actionCase.setActiveEnrolment(false);
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIONPLAN_CHANGED, true);
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).save(actionCase.capture());
    verify(actionCaseRepo, times(1)).flush();
    final List<ActionCase> caze = actionCase.getAllValues();
    assertTrue(caze.get(0).isActiveEnrolment());
  }

  @Test(expected = IllegalStateException.class)
  public void testAcceptNotificationUpdatesActionPlanNoExistingCase() throws Exception {

    // Given
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(null);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIONPLAN_CHANGED, true);
    caseNotificationService.acceptNotification(caseNotification);

    // Then throws IllegalStateException
  }

  @Test
  public void testAcceptNotificationDelete() throws Exception {

    // Given
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.DEACTIVATED, true);
    caseNotificationService.acceptNotification(caseNotification);

    // Then

    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).delete(actionCase.capture());
    verify(actionCaseRepo, times(1)).flush();
  }

  @Test
  public void testAcceptNotificationDeleteNoCase() throws Exception {

    // Given
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(null);

    // When
    caseNotification = createCaseNotification(NotificationType.DEACTIVATED, true);
    caseNotificationService.acceptNotification(caseNotification);

    // Then no case is deleted
    verify(actionCaseRepo, never()).delete(any(ActionCase.class));
  }
}
