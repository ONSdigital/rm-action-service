package uk.gov.ons.ctp.response.action.domain.model;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exportjob", schema = "action")
public class ExportJob {
  @Id private UUID id = UUID.randomUUID();
}
