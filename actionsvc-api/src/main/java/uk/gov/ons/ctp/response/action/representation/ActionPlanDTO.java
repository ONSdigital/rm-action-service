package uk.gov.ons.ctp.response.action.representation;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object for representation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionPlanDTO {

  @NotNull
  private UUID actionPlanId;

  private UUID surveyId;

  private String name;

  private String description;

  private String createdBy;

  private Date lastRunDateTime;
}
