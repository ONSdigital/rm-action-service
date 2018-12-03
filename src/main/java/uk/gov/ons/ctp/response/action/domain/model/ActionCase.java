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
import org.hibernate.annotations.Parameter;

/** Domain model object. */
@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "case", schema = "action")
public class ActionCase implements Serializable {

  private static final long serialVersionUID = 7970373271889255844L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "caseseq_gen")
  @GenericGenerator(
      name = "caseseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "action.casepkseq"),
        @Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "casepk")
  private Integer casePK;

  private UUID id;

  @Column(name = "sampleunit_id")
  private UUID sampleUnitId;

  @Column(name = "actionplanfk")
  private Integer actionPlanFK;

  @Column(name = "actionplanid")
  private UUID actionPlanId;

  @Column(name = "actionplanstartdate")
  private Timestamp actionPlanStartDate;

  @Column(name = "actionplanenddate")
  private Timestamp actionPlanEndDate;

  @Column(name = "collectionexerciseid")
  private UUID collectionExerciseId;

  @Column(name = "partyid")
  private UUID partyId;

  @Column(name = "sampleunittype")
  private String sampleUnitType;

  @Column(name = "processed")
  private Boolean processed;
}
