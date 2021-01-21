package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO.Handler;

@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "action_event", schema = "action")
public class ActionEvent implements Serializable {
  public enum ActionEventStatus {
    PROCESSED,
    FAILED
  }

  private static final long serialVersionUID = 7890373271889255844L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private BigInteger id;

  @Column(name = "case_id")
  @NotNull
  private UUID caseId;

  @Column(name = "type")
  @NotNull
  private String type;

  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  @Column(name = "survey_id")
  private UUID surveyId;

  @Enumerated(EnumType.STRING)
  @Column(name = "handler")
  @NotNull
  private Handler handler;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @NotNull
  private ActionEventStatus status;

  @Column(name = "processed_timestamp")
  private Timestamp processedTimestamp;

  @Column(name = "event_tag")
  private String tag;
}
