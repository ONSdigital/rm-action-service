package uk.gov.ons.ctp.response.action;

import com.godaddy.logging.LoggingConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Clock;
import javax.annotation.PostConstruct;
import net.sourceforge.cobertura.CoverageIgnore;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.state.ActionSvcStateTransitionManagerFactory;
import uk.gov.ons.ctp.response.lib.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.lib.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManagerFactory;

/** The main entry point into the Action Service SpringBoot Application. */
@CoverageIgnore
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@ComponentScan(basePackages = {"uk.gov.ons.ctp.response"})
@EnableJpaRepositories(basePackages = {"uk.gov.ons.ctp.response"})
@EntityScan("uk.gov.ons.ctp.response")
@EnableAsync
@EnableCaching
@EnableScheduling
@ImportResource("springintegration/main.xml")
public class ActionSvcApplication {

  public static final String ACTION_DISTRIBUTION_LIST = "actionsvc.action.distribution";
  public static final String ACTION_EXECUTION_LOCK = "actionsvc.action.execution";
  public static final String REPORT_EXECUTION_LOCK = "actionsvc.report.execution";

  @Autowired private AppConfig appConfig;

  @Autowired private StateTransitionManagerFactory actionSvcStateTransitionManagerFactory;

  /**
   * This method is the entry point to the Spring Boot application.
   *
   * @param args These are the optional command line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(ActionSvcApplication.class, args);
  }

  @PostConstruct
  public void initJsonLogging() {
    if (appConfig.getLogging().isUseJson()) {
      LoggingConfigs.setCurrent(LoggingConfigs.getCurrent().useJson());
    }
  }

  /**
   * Bean used to create and configure Redisson Client
   *
   * @return the Redisson client
   */
  @Bean
  public RedissonClient redissonClient() {
    final Config config = new Config();
    config
        .useSingleServer()
        .setAddress(appConfig.getDataGrid().getAddress())
        .setPassword(appConfig.getDataGrid().getPassword());
    return Redisson.create(config);
  }

  /**
   * The restTemplate bean injected in REST client classes
   *
   * @return the restTemplate used in REST calls
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * Bean used to access case frame service through REST calls
   *
   * @return the service client
   */
  @Bean
  @Qualifier("caseSvcClient")
  public RestUtility caseClient() {
    final RestUtility restUtility = new RestUtility(appConfig.getCaseSvc().getConnectionConfig());
    return restUtility;
  }

  /**
   * Bean used to access collection exercise service through REST calls
   *
   * @return the service client
   */
  @Bean
  @Qualifier("collectionExerciseSvcClient")
  public RestUtility collectionClient() {
    final RestUtility restUtility =
        new RestUtility(appConfig.getCollectionExerciseSvc().getConnectionConfig());
    return restUtility;
  }

  /**
   * Bean used to access party frame service through REST calls
   *
   * @return the service client
   */
  @Bean
  @Qualifier("partySvcClient")
  public RestUtility partyClient() {
    final RestUtility restUtility = new RestUtility(appConfig.getPartySvc().getConnectionConfig());
    return restUtility;
  }

  /**
   * Bean used to access survey service through REST calls
   *
   * @return the service client
   */
  @Bean
  @Qualifier("surveySvcClient")
  public RestUtility surveyClient() {
    final RestUtility restUtility = new RestUtility(appConfig.getSurveySvc().getConnectionConfig());
    return restUtility;
  }

  /**
   * Bean to allow application to make controlled state transitions of Actions
   *
   * @return the state transition manager specifically for Actions
   */
  @Bean
  public StateTransitionManager<ActionDTO.ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager() {
    return actionSvcStateTransitionManagerFactory.getStateTransitionManager(
        ActionSvcStateTransitionManagerFactory.ACTION_ENTITY);
  }

  /**
   * Rest Exception Handler
   *
   * @return a Rest Exception Handler
   */
  @Bean
  public RestExceptionHandler restExceptionHandler() {
    return new RestExceptionHandler();
  }

  /**
   * Custom Object Mapper
   *
   * @return a customer object mapper
   */
  @Bean
  @Primary
  public CustomObjectMapper customObjectMapper() {
    final CustomObjectMapper mapper = new CustomObjectMapper();

    return mapper;
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  /**
   * Bean used to create and configure GCS Client
   *
   * @return the Storage Client
   */
  @Bean
  public Storage storage() {
    return StorageOptions.getDefaultInstance().getService();
  }
}
