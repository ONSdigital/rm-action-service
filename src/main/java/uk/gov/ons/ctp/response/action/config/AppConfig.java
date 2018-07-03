package uk.gov.ons.ctp.response.action.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import uk.gov.ons.tools.rabbit.Rabbitmq;

/** The apps main holder for centralized config read from application.yml or env vars */
@CoverageIgnore
@EnableRetry
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private CaseSvc caseSvc;
  private CollectionExerciseSvc collectionExerciseSvc;
  private PartySvc partySvc;
  private SurveySvc surveySvc;
  private SampleSvc sampleSvc;
  private ActionDistribution actionDistribution;
  private PlanExecution planExecution;
  private CsvIngest csvIngest;
  private DataGrid dataGrid;
  private SwaggerSettings swaggerSettings;
  private ReportSettings reportSettings;
  private Rabbitmq rabbitmq;
}
