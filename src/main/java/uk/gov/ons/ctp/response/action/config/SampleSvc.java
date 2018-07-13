package uk.gov.ons.ctp.response.action.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.rest.RestUtilityConfig;

@CoverageIgnore
@Data
public class SampleSvc {
  private RestUtilityConfig connectionConfig;
  private String sampleAttributesById;
}
