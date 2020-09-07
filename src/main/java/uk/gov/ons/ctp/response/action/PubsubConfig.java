package uk.gov.ons.ctp.response.action;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubsubConfig {

  @Bean
  public TopicName topicName(
      @Value("${gcp.project}") String project, @Value("${gcp.topic}") String topic) {
    return TopicName.of(project, topic);
  }

  @Bean
  public PublisherSupplier publisherSupplier(TopicName topicName) {
    return () -> Publisher.newBuilder(topicName).build();
  }

  @FunctionalInterface
  public static interface PublisherSupplier {

    Publisher get() throws IOException;
  }
}
