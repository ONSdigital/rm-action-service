package uk.gov.ons.ctp.response.action;

import com.godaddy.logging.LoggingConfigs;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Clock;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.lib.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.lib.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;

/** The main entry point into the Action Service SpringBoot Application. */
@CoverageIgnore
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"uk.gov.ons.ctp.response"})
@EnableJpaRepositories(basePackages = {"uk.gov.ons.ctp.response"})
@EntityScan("uk.gov.ons.ctp.response")
@EnableAsync
@EnableCaching
@EnableScheduling
@Slf4j
public class ActionSvcApplication {

  @Autowired private AppConfig appConfig;

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

  /** Bean used to create PubSub action case notification subscription inbound adaptor */
  @Bean
  public PubSubInboundChannelAdapter actionCaseNotificationAdapter(
      @Qualifier("actionCaseNotificationChannel") MessageChannel inputChannel,
      PubSubTemplate pubSubTemplate) {
    String subscriptionName = appConfig.getGcp().getCaseNotificationSubscription();
    log.info("Application started with pubsub subscription id {}", subscriptionName);
    PubSubInboundChannelAdapter adapter =
        new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);
    return adapter;
  }

  /** Bean used to create PubSub action case notification channel */
  @Bean
  public MessageChannel actionCaseNotificationChannel() {
    return new DirectChannel();
  }

  /** Bean used to create PubSub print file channel */
  @Bean
  @ServiceActivator(inputChannel = "printFileChannel")
  public MessageHandler printFileMessageSender(PubSubTemplate pubsubTemplate) {
    return new PubSubMessageHandler(pubsubTemplate, appConfig.getGcp().getPrintFileTopic());
  }

  /** Bean used to publish PubSub print file message */
  @MessagingGateway(defaultRequestChannel = "printFileChannel")
  public interface PubSubOutboundPrintFileGateway {
    void sendToPubSub(String text);
  }

  /** Bean used to create PubSub email channel */
  @Bean
  @ServiceActivator(inputChannel = "notifyEmailChannel")
  public MessageHandler emailMessageSender(PubSubTemplate pubsubTemplate) {
    return new PubSubMessageHandler(pubsubTemplate, appConfig.getGcp().getPrintFileTopic());
  }

  /** Bean used to publish PubSub email message */
  @MessagingGateway(defaultRequestChannel = "notifyEmailChannel")
  public interface PubSubOutboundEmailGateway {
    void sendToPubSub(String text);
  }
}
