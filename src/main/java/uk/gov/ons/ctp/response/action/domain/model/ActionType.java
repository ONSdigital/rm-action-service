package uk.gov.ons.ctp.response.action.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * Domain model object.
 */
@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "actiontype", schema = "action")
public class ActionType implements Serializable {

  private static final long serialVersionUID = -581549382631976704L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "actiontypeseq_gen")
  @GenericGenerator(
          name = "actiontypeseq_gen",
          strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
          parameters = {
                  @org.hibernate.annotations.Parameter(name = "sequence_name", value = "action.actiontypeseq"),
                  @org.hibernate.annotations.Parameter(name = "initial_value", value = "1000"),
                  @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
          }
  )
  @Column(name = "actiontypepk")
  private Integer actionTypePK;

  private String name;
  private String description;
  private String handler;

  @Column(name = "cancancel")
  private Boolean canCancel;


  @Column(name = "responserequired")
  private Boolean responseRequired;

}
