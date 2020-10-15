package uk.gov.ons.ctp.response.action.config;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrintfilePubsubConfig {

  @Bean("printfile")
  public Publisher printFilePublisherSupplier(
      @Value("${gcp.project}") String project, @Value("${gcp.printfile.topic}") String topic)
      throws IOException {
    TopicName topicName = TopicName.of(project, topic);
    return Publisher.newBuilder(topicName).build();
  }
}
