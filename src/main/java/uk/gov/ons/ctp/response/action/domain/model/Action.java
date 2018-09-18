package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;

/** Domain model object. */
@CoverageIgnore
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "action", schema = "action")
public class Action implements Serializable {

  private static final long serialVersionUID = 8539984354009320104L;
  public static final String CREATED_BY_SYSTEM = "SYSTEM";
  public static final int DEFAULT_PRIORITY = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "actionpk")
  private BigInteger actionPK;

  private UUID id;

  @Column(name = "caseid")
  private UUID caseId;

  @Column(name = "casefk")
  private Integer caseFK;

  @Column(name = "actionplanfk")
  private Integer actionPlanFK;

  @Column(name = "actionrulefk")
  private Integer actionRuleFK;

  @Column(name = "createdby")
  private String createdBy;

  @Column(name = "manuallycreated")
  private Boolean manuallyCreated;

  @ManyToOne
  @JoinColumn(name = "actiontypefk")
  private ActionType actionType;

  private Integer priority;
  private String situation;

  @Enumerated(EnumType.STRING)
  @Column(name = "statefk")
  private ActionState state;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;

  @Column(name = "updateddatetime")
  private Timestamp updatedDateTime;

  @Version
  @Column(name = "optlockversion")
  private int optLockVersion;

  /** Priority of action NOTE: the names need to match those in the outbound xsd */
  public enum ActionPriority {
    HIGHEST(1, "highest"),
    HIGHER(2, "higher"),
    MEDIUM(3, "medium"),
    LOWER(4, "lower"),
    LOWEST(5, "lowest");

    private static Map<Integer, ActionPriority> map = new HashMap<Integer, ActionPriority>();

    static {
      for (final ActionPriority priority : ActionPriority.values()) {
        map.put(priority.level, priority);
      }
    }

    private final int level; // numeric level
    private final String name; // the level name

    /**
     * Create an instance of the enum
     *
     * @param value priority as integer
     * @param label verbage
     */
    ActionPriority(final int value, final String label) {
      this.level = value;
      this.name = label;
    }

    /**
     * return the enum for an integer level arg
     *
     * @param priorityLevel the int value
     * @return the enum
     */
    public static ActionPriority valueOf(final int priorityLevel) {
      return map.get(priorityLevel);
    }

    public String getName() {
      return this.name;
    }
  }
}
