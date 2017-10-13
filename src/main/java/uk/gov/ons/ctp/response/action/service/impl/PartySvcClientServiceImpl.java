package uk.gov.ons.ctp.response.action.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;

import java.io.IOException;
import java.util.UUID;

/**
 * Impl of the service that centralizes all REST calls to the Party service
 *
 */
@Slf4j
@Service
public class PartySvcClientServiceImpl implements PartySvcClientService {

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  @Qualifier("partySvcClient")
  private RestUtility restUtility;
  
  @Autowired
  private ObjectMapper objectMapper;

  @Retryable(value = {
      RestClientException.class}, maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  @Override
  public PartyDTO getParty(final String sampleUnitType, final UUID partyId) {
    log.info("entering getParty with sampleUnitType {} - partyId {}", sampleUnitType, partyId);
    UriComponents uriComponents = restUtility.createUriComponents(
        appConfig.getPartySvc().getPartyBySampleUnitTypeAndIdPath(), null, sampleUnitType, partyId);
    
    HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);
    
    ResponseEntity<String> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity,
        String.class);
    log.info("responseEntity is {}", responseEntity);
    
    PartyDTO result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      log.info("responseBody is {}", responseBody);
      try {
        log.info("about to map to PartyDTO...");
        result = objectMapper.readValue(responseBody, PartyDTO.class);
        log.info("result is {}", result);
      } catch (IOException e) {
        String msg = String.format("zzzyzzz cause = %s - message = %s", e.getCause(), e.getMessage());
        log.error(msg);
        log.error("Stacktrace: ", e);
      }
    }

    log.info("PARTY GOTTEN {}", result);
    return result;
  }
}
