package uk.gov.ons.ctp.response.action.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PotentialAction {

  private UUID caseId;

  private Integer caseFk;

  private Integer actionPlanFk;

  private Integer actionRuleFk;

  private ActionType actionType;

  private Integer priority;
}
