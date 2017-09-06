package uk.gov.ons.ctp.response.action.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

/**
 * App config POJO for survey service access - host location and endpoint
 * locations
 */
@CoverageIgnore
@Data
public class SurveySvc {
  private RestClientConfig connectionConfig;
  private String requestSurveyPath;
}
