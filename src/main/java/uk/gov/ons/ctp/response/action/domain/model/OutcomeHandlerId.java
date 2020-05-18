package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionEvent;

/** Domain model object. */
@CoverageIgnore
@Data
@Builder
@Embeddable
@AllArgsConstructor
public class OutcomeHandlerId implements Serializable {
  private static final long serialVersionUID = 3161993644341999008L;

  @Enumerated(EnumType.STRING)
  @Column(name = "actionoutcomepk")
  private ActionEvent actionOutcome;

  @Column(name = "handlerpk")
  private String handler;
}
