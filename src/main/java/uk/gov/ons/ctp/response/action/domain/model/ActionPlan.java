package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
@Table(name = "actionplan", schema = "action")
public class ActionPlan implements Serializable {

  private static final long serialVersionUID = 3621028547635970347L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "actionplanseq_gen")
  @GenericGenerator(
      name = "actionplanseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @org.hibernate.annotations.Parameter(
            name = "sequence_name",
            value = "action.actionplanseq"),
        @org.hibernate.annotations.Parameter(name = "initial_value", value = "1000"),
        @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "actionplanpk")
  private Integer actionPlanPK;

  private UUID id;

  private String name;

  private String description;

  @Column(name = "createdby")
  private String createdBy;

  @Column(name = "lastrundatetime")
  private Timestamp lastRunDateTime;
}
