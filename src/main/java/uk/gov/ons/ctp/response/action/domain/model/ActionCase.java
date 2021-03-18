package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.*;
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
  @Column(name = "case_pk")
  private Integer casePK;

  private UUID id;

  @Column(name = "sample_unit_id")
  private UUID sampleUnitId;

  @Column(name = "action_plan_fk")
  private Integer actionPlanFK;

  @Column(name = "action_plan_id")
  private UUID actionPlanId;

  @Column(name = "active_enrolment")
  private boolean activeEnrolment;

  @Column(name = "action_plan_start_date")
  private Timestamp actionPlanStartDate;

  @Column(name = "action_plan_end_date")
  private Timestamp actionPlanEndDate;

  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  @Column(name = "party_id")
  private UUID partyId;

  @Column(name = "sampleunittype")
  private String sampleUnitType;

  @Column(name = "sample_unit_ref")
  private String sampleUnitRef;

  @Column(name = "status")
  private String status;

  @Column(name = "iac")
  private String iac;
}
