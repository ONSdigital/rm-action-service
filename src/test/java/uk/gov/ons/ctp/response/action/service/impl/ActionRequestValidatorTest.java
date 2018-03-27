package uk.gov.ons.ctp.response.action.service.impl;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;


@RunWith(MockitoJUnitRunner.class)
public class ActionRequestValidatorTest {

    private ActionRequestValidator validator = new ActionRequestValidator();

    @Test
    public void testValidNotificationLetter() {
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.NOTSTARTED.toString())
                .withEnrolmentStatus(ActionProcessingServiceImpl.PENDING)
                .withRespondentStatus(ActionRequestValidator.RESPONDENTCREATED).build();

        assertTrue(validator.validate(actionRequest));
    }

    @Test
    public void testInvalidNotificationLetterCaseCompleted() {
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.COMPLETE.toString()).build();

        assertFalse(validator.validate(actionRequest));
    }

    @Test
    public void testInvalidNotificationLetterCaseCompletedByPhone() {
        ActionRequest actionRequest = ActionRequest.builder().withCaseGroupStatus(CaseGroupStatus.COMPLETEDBYPHONE.toString()).build();

        assertFalse(validator.validate(actionRequest));
    }

}
