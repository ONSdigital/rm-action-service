package uk.gov.ons.ctp.response.action.domain.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

/** Domain model object. */
@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(name = "actionplan", schema = "action")
public class ActionPlan implements Serializable {

  private static final long serialVersionUID = 3621028547635970347L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "actionplanseq_gen")
  @GenericGenerator(
      name = "actionplanseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "action.actionplanseq"),
        @Parameter(name = "initial_value", value = "1000"),
        @Parameter(name = "increment_size", value = "1"),
        @Parameter(name = "sequence_name", value = "action.actionplanseq"),
      })
  @Column(name = "action_plan_pk")
  private Integer actionPlanPK;

  private UUID id;

  private String name;

  private String description;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "last_run_date_time")
  private Timestamp lastRunDateTime;

  @Type(type = "jsonb")
  @Column(name = "selectors", columnDefinition = "jsonb")
  private HashMap<String, String> selectors;
}
