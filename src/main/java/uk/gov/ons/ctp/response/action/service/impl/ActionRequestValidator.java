package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

@Service
public class ActionRequestValidator {

    public static final String RESPONDENTCREATED = "CREATED";
    public static final String RESPONDENTACTIVE= "ACTIVE";

    public boolean validate(ActionType actionType, ActionRequest actionRequest) {
        // Completed no action required
        if(actionRequest.getCaseGroupStatus().equals(CaseGroupStatus.COMPLETE.toString())) {
            return false;
        }

        if (hasActiveRespondent(actionRequest) && enrolmentEnabled(actionRequest)) {
            return validateEmail(actionType, actionRequest);
        }

        if (hasCreatedRespondent(actionRequest)) {
            return validateLetter(actionType, actionRequest);
        }
        return false;
    }


    private boolean validateEmail(final ActionType actionType, final ActionRequest actionRequest) {
        if (isNotificationEmail(actionType)) {
            return true;
        }
        if (isReminderEmail(actionType) && (caseInProgress(actionRequest) || caseNotStarted(actionRequest))) {
            return true;
        }
        return false;
    }

    private boolean validateLetter(final ActionType actionType, final ActionRequest actionRequest) {
        if(isNotificationLetter(actionType) && enrolmentPending(actionRequest)) {
            return true;
        }
        if (isReminderLetter(actionType) && (enrolmentPending(actionRequest) || enrolmentEnabled(actionRequest))) {
            return true;
        }
        return false;

    }

    private boolean isNotificationLetter(final ActionType actionType) {
        return actionType.getActionTypePK().equals(1);
    }

    private boolean isReminderLetter(final ActionType actionType) {
        return actionType.getActionTypePK().equals(2);
    }

    private boolean isReminderEmail(final ActionType actionType) {
        return actionType.getActionTypePK().equals(3);
    }

    private boolean isNotificationEmail(final ActionType actionType) {
        //TODO: Currently not available in system but will be added
        return actionType.getActionTypePK().equals(4);
    }

    private boolean caseInProgress(final ActionRequest actionRequest) {
        return actionRequest.getCaseGroupStatus().equals(CaseGroupStatus.INPROGRESS.toString());
    }

    private boolean caseNotStarted(final ActionRequest actionRequest) {
        return actionRequest.getCaseGroupStatus().equals(CaseGroupStatus.NOTSTARTED.toString());
    }

    private boolean enrolmentPending(final ActionRequest actionRequest) {
        return actionRequest.getEnrolmentStatus().equals(ActionProcessingServiceImpl.PENDING);
    }

    private boolean enrolmentEnabled(final ActionRequest actionRequest) {
        return actionRequest.getEnrolmentStatus().equals(ActionProcessingServiceImpl.ENABLED);
    }

    private boolean hasActiveRespondent(final ActionRequest actionRequest) {
        return actionRequest.getRespondentStatus().equals(RESPONDENTACTIVE);
    }

    private boolean hasCreatedRespondent(final ActionRequest actionRequest) {
        return actionRequest.getRespondentStatus().equals(RESPONDENTCREATED);
    }

}
