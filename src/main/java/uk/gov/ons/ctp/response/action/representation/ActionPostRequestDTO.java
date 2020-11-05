package uk.gov.ons.ctp.response.action.representation;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object for representation. */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionPostRequestDTO {

  @NotNull private UUID caseId;

  @NotNull private String actionTypeName;

  @NotNull private String createdBy;

  private Integer priority;
}
