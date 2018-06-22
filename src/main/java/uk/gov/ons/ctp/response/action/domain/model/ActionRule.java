package uk.gov.ons.ctp.response.action.domain.model;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import org.hibernate.annotations.GenericGenerator;

/** Domain model object. */
@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "actionrule", schema = "action")
public class ActionRule {

  private UUID id;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "actionruleseq_gen")
  @GenericGenerator(
      name = "actionruleseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @org.hibernate.annotations.Parameter(
            name = "sequence_name",
            value = "action.actionruleseq"),
        @org.hibernate.annotations.Parameter(name = "initial_value", value = "1000"),
        @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "actionrulepk")
  private Integer actionRulePK;

  @Column(name = "actionplanfk")
  private Integer actionPlanFK;

  @Column(name = "actiontypefk")
  private Integer actionTypeFK;

  private String name;

  private String description;

  @Column(name = "daysoffset")
  private Integer daysOffset;

  private Integer priority;
}
