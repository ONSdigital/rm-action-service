package uk.gov.ons.ctp.response.action.service.impl;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;


@RunWith(MockitoJUnitRunner.class)
public class ActionRequestValidatorTest {

    private ActionRequestValidator validator = new ActionRequestValidator();

    @Test
    public void testValidNotificationLetter() {
        ActionType actionType = buildNotificationLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString())
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
    public void testInvalidNotificationLetterCaseCompletedByPhone() {
        ActionType actionType = buildNotificationLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.COMPLETEDBYPHONE.toString()).build();

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
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testValidReminderLetterNoActivity() {
        ActionType actionType = buildReminderLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder()
                .withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString()).build();

        assertTrue(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidReminderLetterInvalidEnrolment() {
        ActionType actionType = buildReminderLetterActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus("SUSPENDED")
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
    public void testInvalidRemindEmailNullEnromentStatus() {
        ActionType actionType = buildReminderEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidReminderEmailInvalidEnrolment() {
        ActionType actionType = buildReminderEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString())
                .withEnrolmentStatus("SUSPENDED")
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
    public void testInvalidNotificationEmailInvalidRespondentStatus() {
        ActionType actionType = buildNotificationEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }

    @Test
    public void testInvalidNotificationEmailNullRespondentStatus() {
        ActionType actionType = buildNotificationEmailActionType();
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.INPROGRESS.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.ENABLED).build();

        assertFalse(validator.validate(actionType, actionRequest));
    }


    private ActionType buildNotificationLetterActionType() {
        return ActionType.builder().actionTypePK(1).name("BSNOT").handler(ActionRequestValidator.ACTIONEXPORTER).build();
    }

    private ActionType buildReminderLetterActionType() {
        return ActionType.builder().actionTypePK(2).name("BSREM").handler(ActionRequestValidator.ACTIONEXPORTER).build();
    }

    private ActionType buildReminderEmailActionType() {
        return ActionType.builder().actionTypePK(3).name("BSNE").handler(ActionRequestValidator.NOTIFYGATEWAY).build();
    }

    private ActionType buildNotificationEmailActionType() {
        return ActionType.builder().actionTypePK(4).name("UNKNOWN").handler(ActionRequestValidator.NOTIFYGATEWAY).build();
    }
}
