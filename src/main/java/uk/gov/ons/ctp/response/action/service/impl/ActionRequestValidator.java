package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

@Slf4j
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
        if (caseCompleted(actionRequest)) {
            return false;
        }

        if (isEmail(handler) && hasActiveRespondent(actionRequest) && enrolmentEnabled(actionRequest)) {
            return validateEmail(actionType, actionRequest);
        }

        if (isLetter(handler)) {
            return validateLetter(actionRequest);
        }

        log.info("Invalid action request: handler = {} : respondentStatus {} :  enrolmentStatus {} : actionTypePK {}",
                handler, actionRequest.getCaseGroupStatus(), actionRequest.getEnrolmentStatus(),
                actionType.getActionTypePK());

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

    private boolean validateLetter(final ActionRequest actionRequest) {
        if (hasNoRespondent(actionRequest) && hasNoEnrolment(actionRequest) && caseNotStarted(actionRequest)) {
            return true;
        }
        if (enrolmentPending(actionRequest) && hasCreatedRespondent(actionRequest) && caseNotStarted(actionRequest)) {
            return true;
        }
        return false;
    }

    private boolean isLetter(final String handler) {
        return ACTIONEXPORTER.equalsIgnoreCase(handler);
    }

    private boolean isEmail(final String handler) {
        return NOTIFYGATEWAY.equalsIgnoreCase(handler);
    }

    private boolean isReminderEmail(final ActionType actionType) {
        return actionType.getActionTypePK() != null && actionType.getActionTypePK().equals(3);
    }

    private boolean isNotificationEmail(final ActionType actionType) {
        return actionType.getActionTypePK() != null && actionType.getActionTypePK().equals(4);
    }

    private boolean caseCompleted(final ActionRequest actionRequest) {
        return CaseGroupStatus.COMPLETE.toString().equalsIgnoreCase(actionRequest.getCaseGroupStatus());
    }

    private boolean caseInProgress(final ActionRequest actionRequest) {
        return CaseGroupStatus.INPROGRESS.toString().equalsIgnoreCase(actionRequest.getCaseGroupStatus());
    }

    private boolean caseNotStarted(final ActionRequest actionRequest) {
        return CaseGroupStatus.NOTSTARTED.toString().equalsIgnoreCase(actionRequest.getCaseGroupStatus());
    }

    private boolean enrolmentPending(final ActionRequest actionRequest) {
        return ActionProcessingServiceImpl.PENDING.equalsIgnoreCase(actionRequest.getEnrolmentStatus());
    }

    private boolean enrolmentEnabled(final ActionRequest actionRequest) {
        return ActionProcessingServiceImpl.ENABLED.equalsIgnoreCase(actionRequest.getEnrolmentStatus());
    }

    private boolean hasNoEnrolment(final ActionRequest actionRequest) {
        return actionRequest.getEnrolmentStatus() == null;
    }

    private boolean hasActiveRespondent(final ActionRequest actionRequest) {
        return RESPONDENTACTIVE.equalsIgnoreCase(actionRequest.getRespondentStatus());
    }

    private boolean hasCreatedRespondent(final ActionRequest actionRequest) {
        return RESPONDENTCREATED.equalsIgnoreCase(actionRequest.getRespondentStatus());
    }

    private boolean hasNoRespondent(final ActionRequest actionRequest) {
        return actionRequest.getRespondentStatus() == null;
    }

}
