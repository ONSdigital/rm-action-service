package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.ActionSvcApplication;
import uk.gov.ons.ctp.response.action.config.AppConfig;

@Service
public class NotifyEmailService {
  public static Logger log = LoggerFactory.getLogger(NotifyEmailService.class);

  @Autowired AppConfig appConfig;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ActionSvcApplication.PubSubOutboundEmailGateway pubEmailPublisher;

  public void processEmail(NotifyModel notifyPayload) {

    log.debug("Sending notification to pubsub");

    try {
      String message = objectMapper.writeValueAsString(notifyPayload);
      pubEmailPublisher.sendToPubSub(message);
    } catch (JsonProcessingException e) {
      log.error("Error converting an actionRequest to JSON", e);
      throw new RuntimeException(e);
    }
  }
}
