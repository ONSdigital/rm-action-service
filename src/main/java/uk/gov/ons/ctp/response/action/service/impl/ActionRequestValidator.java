package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
     * Checks if the CaseGroupStatus is completed, this stops any actions beings sent for BI
     * Cases where other BI's have completed the survey
     */
    public boolean validate(final ActionRequest actionRequest) {
        return !(caseCompleted(actionRequest) || caseCompletedByPhone(actionRequest));
    }

    private boolean caseCompleted(final ActionRequest actionRequest) {
        return CaseGroupStatus.COMPLETE.toString().equalsIgnoreCase(actionRequest.getCaseGroupStatus());
    }

    private boolean caseCompletedByPhone(final ActionRequest actionRequest) {
        return CaseGroupStatus.COMPLETEDBYPHONE.toString().equalsIgnoreCase(actionRequest.getCaseGroupStatus());
    }

}
