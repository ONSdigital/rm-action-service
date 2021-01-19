package uk.gov.ons.ctp.response.action.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;

@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "action_partial_event_process_entry", schema = "action")
public class ActionEventPartialEntry {
  public enum ActionEventPartialProcessStatus {
    COMPLETED,
    PARTIAL
  }

  private static final long serialVersionUID = 7890393271889955844L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private BigInteger id;

  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  @Column(name = "event_tag_mapping")
  @NotNull
  private String eventTag;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @NotNull
  private ActionEventPartialEntry.ActionEventPartialProcessStatus status;

  @Column(name = "processed_cases")
  @NotNull
  private Long processedCases;

  @Column(name = "pending_cases")
  @NotNull
  private Long pendingCases;

  @Column(name = "last_processed_timestamp")
  private Timestamp lastProcessedTimestamp;
}
