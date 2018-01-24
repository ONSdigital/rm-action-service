package uk.gov.ons.ctp.response.action.service.impl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.action.service.impl.ActionRequestValidator.*;


@RunWith(MockitoJUnitRunner.class)
public class ActionRequestValidatorTest {

    @InjectMocks
    private ActionRequestValidator validator;

    /**
     * Initialises Mockito and loads Class Fixtures
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidNotificationLetter() {
        ActionType actionType = buildNotificationLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidNotificationLetterCaseCompleted() {
        ActionType actionType = buildNotificationLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.COMPLETE.toString()).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidNotificationLetterEnrolmentNotPending() {
        ActionType actionType = buildNotificationLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidNotifidcationLetterRespondentNotCreated() {
        ActionType actionType = buildNotificationLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }


    @Test
    public void testValidReminderLetterPendingEnrolment() {
        ActionType actionType = buildReminderLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testValidReminderLetterEnabledEnrolment() {
        ActionType actionType = buildReminderLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidReminderLetterInvalidEnrolment() {
        ActionType actionType = buildReminderLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.SUSPENDED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED)
                .build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidReminderLetterInvalidRespondentStatus() {
        ActionType actionType = buildReminderLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE)
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testValidReminderEmailCaseInProgress() {
        ActionType actionType = buildReminderEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testValidReminderEmailCaseNotStarted() {
        ActionType actionType = buildReminderEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidReminderEmailInvalidRespondentStatus() {
        ActionType actionType = buildReminderEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }
    @Test
    public void testInvalidReminderEmailInvalidEnrolment() {
        ActionType actionType = buildReminderEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testValidNotificationEmail() {
        ActionType actionType = buildNotificationEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE)
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidNotificationEmailInvalidEnrolment() {
        ActionType actionType = buildNotificationEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withRespondentStatus(ActionRequestValidator.RESPONDENTACTIVE)
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidNotificationEmailInvaliRespondentStatus() {
        ActionType actionType = buildNotificationEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }


    private ActionType buildNotificationLetterActionType() {
        return ActionType.builder().actionTypePK(1).name("BSNOT").build();
    }

    private ActionType buildReminderLetterActionType() {
        return ActionType.builder().actionTypePK(2).name("BSREM").build();
    }

    private ActionType buildReminderEmailActionType() {
        return ActionType.builder().actionTypePK(3).name("BSNE").build();
    }

    private ActionType buildNotificationEmailActionType() {
        //TODO: Update name when we actually populate the systems with this actiontype
        return ActionType.builder().actionTypePK(4).name("UNKNOWN").build();
    }
}
