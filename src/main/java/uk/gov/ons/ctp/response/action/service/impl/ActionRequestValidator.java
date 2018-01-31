package uk.gov.ons.ctp.response.action.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

@Service
public class ActionRequestValidator {

    public static final String RESPONDENTCREATED = "CREATED";
    public static final String RESPONDENTACTIVE= "ACTIVE";
    public static final String NOTIFYGATEWAY = "NOTIFY";
    public static final String ACTIONEXPORTER = "PRINTER";

    /**
     * Validates whether the ActionRequest should be sent to a handler service.
     * Contains the business logic for deciding whether the recipient is to receive an email or letter,
     * dependent on the status of their account and the response.
     * @param actionType
     * @param actionRequest
     * @return isValid
     */
    public boolean validate(final ActionType actionType, final ActionRequest actionRequest) {
        String handler = actionType.getHandler();
        // Completed no action required
        if (actionRequest.getCaseGroupStatus().equals(CaseGroupStatus.COMPLETE.toString())) {
            return false;
        }

        if (isEmail(handler) && hasActiveRespondent(actionRequest) && enrolmentEnabled(actionRequest)) {
            return validateEmail(actionType, actionRequest);
        }

        if (isLetter(handler) && hasCreatedRespondent(actionRequest)) {
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

    private boolean isLetter(final String handler) {
        return handler.equalsIgnoreCase(ACTIONEXPORTER);
    }

    private boolean isEmail(final String handler) {
        return handler.equalsIgnoreCase(NOTIFYGATEWAY);
    }

    private boolean isNotificationLetter(final ActionType actionType) {
        return actionType.getActionTypePK() != null && actionType.getActionTypePK().equals(1);
    }

    private boolean isReminderLetter(final ActionType actionType) {
        return actionType.getActionTypePK() != null && actionType.getActionTypePK().equals(2);
    }

    private boolean isReminderEmail(final ActionType actionType) {
        return actionType.getActionTypePK() != null && actionType.getActionTypePK().equals(3);
    }

    private boolean isNotificationEmail(final ActionType actionType) {
        //TODO: Currently not available in system but will be added
        return actionType.getActionTypePK() != null && actionType.getActionTypePK().equals(4);
    }

    private boolean caseInProgress(final ActionRequest actionRequest) {
        return actionRequest.getCaseGroupStatus().equalsIgnoreCase(CaseGroupStatus.INPROGRESS.toString());
    }

    private boolean caseNotStarted(final ActionRequest actionRequest) {
        return actionRequest.getCaseGroupStatus().equalsIgnoreCase(CaseGroupStatus.NOTSTARTED.toString());
    }

    private boolean enrolmentPending(final ActionRequest actionRequest) {
        return actionRequest.getEnrolmentStatus() != null && actionRequest.getEnrolmentStatus().equalsIgnoreCase(ActionProcessingServiceImpl.PENDING);
    }

    private boolean enrolmentEnabled(final ActionRequest actionRequest) {
        return actionRequest.getEnrolmentStatus() != null && actionRequest.getEnrolmentStatus().equalsIgnoreCase(ActionProcessingServiceImpl.ENABLED);
    }

    private boolean hasActiveRespondent(final ActionRequest actionRequest) {
        return actionRequest.getRespondentStatus() != null && actionRequest.getRespondentStatus().equalsIgnoreCase(RESPONDENTACTIVE);
    }

    private boolean hasCreatedRespondent(final ActionRequest actionRequest) {
        return actionRequest.getRespondentStatus() != null && actionRequest.getRespondentStatus().equalsIgnoreCase(RESPONDENTCREATED);
    }

}
