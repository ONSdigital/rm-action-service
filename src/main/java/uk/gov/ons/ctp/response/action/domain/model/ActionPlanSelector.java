package uk.gov.ons.ctp.response.action.domain.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.HashMap;
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
@Table(name = "actionplanselector", schema = "action")
public class ActionPlanSelector {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "actionplanselectorseq_gen")
  @GenericGenerator(
      name = "actionplanselectorseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @org.hibernate.annotations.Parameter(
            name = "sequence_name",
            value = "action.actionplanselectorseq"),
        @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "actionplanselectorpk")
  private Integer actionPlanSelectorPK;

  @Column(name = "actionplanfk")
  private Integer actionPlanFk;

  @Type(type = "jsonb")
  @Column(name = "selectors", columnDefinition = "jsonb")
  private HashMap<String, String> selectors;
}
