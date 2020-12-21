package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "action_template", schema = "action")
public class ActionTemplate implements Serializable {
  private static final long serialVersionUID = 7778360895016862376L;

  @Id
  @Column(name = "type")
  @NotNull
  private String type;

  @Column(name = "description")
  @NotNull
  private String description;

  @Column(name = "event_tag_mapping")
  @NotNull
  private String tag;

  @Enumerated(EnumType.STRING)
  @Column(name = "handler")
  @NotNull
  private ActionTemplateDTO.Handler handler;

  @Column(name = "prefix")
  private String prefix;
}
