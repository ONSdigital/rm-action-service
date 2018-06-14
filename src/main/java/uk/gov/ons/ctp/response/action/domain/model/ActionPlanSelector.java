package uk.gov.ons.ctp.response.action.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.UUID;

/**
 * Domain model object.
 */
@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "actionplanselector", schema = "action")
public class ActionPlanSelector {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "actionplanselectorseq_gen")
  @GenericGenerator(
          name = "actionplanselectorseq_gen",
          strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
          parameters = {
                  @org.hibernate.annotations.Parameter(name = "sequence_name", value = "action.actionplanselectorseq"),
                  @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
          }
  )
  @Column(name = "actionplanselectorpk")
  private Integer actionPlanSelectorPK;

  @Column(name = "actionplanfk")
  private UUID actionPlanFk;

  @Column(name = "selectors")
  private HashMap<String, String> selectors;

}
