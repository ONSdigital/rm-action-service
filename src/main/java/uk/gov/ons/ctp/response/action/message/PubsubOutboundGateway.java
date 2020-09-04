package uk.gov.ons.ctp.response.action.message;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
public interface PubsubOutboundGateway {

  void sendToPubsub(String text);
}