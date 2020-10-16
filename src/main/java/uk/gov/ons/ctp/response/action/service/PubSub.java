package uk.gov.ons.ctp.response.action.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.config.AppConfig;

@Log4j
@Component
public class PubSub {

  @Autowired AppConfig appConfig;

  private Publisher publisherSupplier(String project, String topic) throws IOException {
    log.info("creating pubsub publish for topic " + topic + " in project " + project);
    TopicName topicName = TopicName.of(project, topic);
    return Publisher.newBuilder(topicName).build();
  }

  public Publisher notifyPublisher() throws IOException {
    return publisherSupplier(appConfig.getGcp().getProject(), appConfig.getGcp().getNotifyTopic());
  }

  public Publisher printfilePublisher() throws IOException {
    return publisherSupplier(
        appConfig.getGcp().getProject(), appConfig.getGcp().getPrintFileTopic());
  }
}
