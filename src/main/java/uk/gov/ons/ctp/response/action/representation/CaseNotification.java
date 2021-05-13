package uk.gov.ons.ctp.response.action.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.lib.casesvc.message.notification.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseNotification {
  protected String sampleUnitId;
  protected String caseId;
  protected String actionPlanId;
  protected boolean activeEnrolment;
  protected String exerciseId;
  protected String partyId;
  protected String sampleUnitType;
  protected NotificationType notificationType;
  protected String sampleUnitRef;
  protected String status;
  protected String iac;
}
