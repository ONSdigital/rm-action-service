package uk.gov.ons.ctp.response.action.scheduled.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.response.action.scheduled.distribution.DistributionInfo.Instruction;

/**
 * Simple pojo for health info re instructions of a type sent
 *
 */
@CoverageIgnore
@Data
@Builder
@AllArgsConstructor
class InstructionCount {
  private String actionTypeName;
  private Instruction instruction;
  private Integer count;

  void increment() {
    count += 1;
  }
}
