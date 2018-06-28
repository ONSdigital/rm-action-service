package uk.gov.ons.ctp.response.action.domain.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
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
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

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
        @Parameter(name = "increment_size", value = "1")
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

  @Type(type = "jsonb")
  @Column(name = "selectors", columnDefinition = "jsonb")
  private HashMap<String, String> selectors;
}
