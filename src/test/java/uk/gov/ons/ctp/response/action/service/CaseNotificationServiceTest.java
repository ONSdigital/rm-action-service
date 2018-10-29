package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/** Tests for the CaseNotificationServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class CaseNotificationServiceTest {

  private static final String DUMMY_UUID = "7bc5d41b-0549-40b3-ba76-42f6d4cf3991";

  @Mock private ActionCaseRepository actionCaseRepo;
  @Mock private ActionPlanRepository actionPlanRepo;

  @Mock private ActionService actionService;
  @Mock private CollectionExerciseClientService collectionSvcClientServiceImpl;
  @Mock private SocialActionProcessingService socialActionProcessingServiceImpl;

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

  private CaseNotification createCaseNotification(NotificationType notificationType) {
    final CaseNotification caseNotification = new CaseNotification();
    caseNotification.setActionPlanId(DUMMY_UUID);
    caseNotification.setCaseId(DUMMY_UUID);
    caseNotification.setNotificationType(notificationType);
    caseNotification.setExerciseId(DUMMY_UUID);
    caseNotification.setPartyId(DUMMY_UUID);
    caseNotification.setSampleUnitId(DUMMY_UUID);
    return caseNotification;
  }

  @Test
  public void testAcceptNotification() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(collectionSvcClientServiceImpl.getCollectionExercise(UUID.fromString(DUMMY_UUID)))
        .thenReturn(collectionExercises.get(0));

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIVATED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).save(actionCase.capture());
    final List<ActionCase> caze = actionCase.getAllValues();

    verify(actionCaseRepo, times(1)).flush();
    assertEquals(UUID.fromString(DUMMY_UUID), caze.get(0).getActionPlanId());
    assertNotNull(caze.get(0).getActionPlanStartDate());
    assertNotNull(caze.get(0).getActionPlanEndDate());
    assertEquals(UUID.fromString(DUMMY_UUID), caze.get(0).getId());
  }

  @Test(expected = IllegalStateException.class)
  public void testAcceptNotificationNoActionPlan() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(null);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIVATED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then throws an IllegalStateException
  }

  @Test(expected = IllegalStateException.class)
  public void testAcceptNotificationActionCaseAlreadyExists() throws Exception {

    // Given a action case for DUMMY_UUID already exists
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(collectionSvcClientServiceImpl.getCollectionExercise(UUID.fromString(DUMMY_UUID)))
        .thenReturn(collectionExercises.get(0));
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIVATED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then throws an IllegalStateException
  }

  @Test
  public void testAcceptNotificationUpdatesActionPlan() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIONPLAN_CHANGED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).save(actionCase.capture());
    verify(actionCaseRepo, times(1)).flush();
  }

  @Test(expected = IllegalStateException.class)
  public void testAcceptNotificationUpdatesActionPlanNoExistingCase() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(null);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIONPLAN_CHANGED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then throws IllegalStateException
  }

  @Test(expected = IllegalStateException.class)
  public void testAcceptNotificationUpdatesActionPlanNoPlan() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(null);
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.ACTIONPLAN_CHANGED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then throws IllegalStateException
  }

  @Test
  public void testAcceptNotificationDelete() throws Exception {

    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(actionCase);

    // When
    caseNotification = createCaseNotification(NotificationType.DEACTIVATED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    verify(actionService, times(1)).cancelActions(UUID.fromString(DUMMY_UUID));

    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).delete(actionCase.capture());
    verify(actionCaseRepo, times(1)).flush();
  }

  @Test
  public void testSocialAcceptNotificationDeactivated() throws Exception {
    // Given
    when(actionPlanRepo.findById(any())).thenReturn(actionPlan);
    ActionCase ac = new ActionCase();
    ac.setSampleUnitType("H");
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(ac);

    // When
    caseNotification = createCaseNotification(NotificationType.DEACTIVATED);
    caseNotification.setSampleUnitType("H");
    caseNotificationService.acceptNotification(caseNotification);

    // Then
    verify(actionService, times(1)).cancelActions(UUID.fromString(DUMMY_UUID));
    verify(socialActionProcessingServiceImpl, times(1))
        .cancelFieldWorkReminder(UUID.fromString(DUMMY_UUID));
    final ArgumentCaptor<ActionCase> actionCase = ArgumentCaptor.forClass(ActionCase.class);
    verify(actionCaseRepo, times(1)).delete(actionCase.capture());
    verify(actionCaseRepo, times(1)).flush();
  }

  @Test
  public void testAcceptNotificationDeleteNoCase() throws Exception {

    // Given
    when(actionCaseRepo.findById(UUID.fromString(DUMMY_UUID))).thenReturn(null);

    // When
    caseNotification = createCaseNotification(NotificationType.DEACTIVATED);
    caseNotificationService.acceptNotification(caseNotification);

    // Then no case is deleted
    verify(actionCaseRepo, never()).delete(any(ActionCase.class));
  }
}
