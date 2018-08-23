package uk.gov.ons.ctp.response.action.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;

/** Impl of the service that centralizes all REST calls to the Party service */
@Service
public class PartySvcClientService {
  private static final Logger log = LoggerFactory.getLogger(PartySvcClientService.class);

  private AppConfig appConfig;
  private RestTemplate restTemplate;
  private RestUtility restUtility;
  private ObjectMapper objectMapper;

  public PartySvcClientService(
      AppConfig appConfig,
      RestTemplate restTemplate,
      @Qualifier("partySvcClient") RestUtility restUtility,
      @Qualifier("customObjectMapper") ObjectMapper objectMapper) {
    this.appConfig = appConfig;
    this.restTemplate = restTemplate;
    this.restUtility = restUtility;
    this.objectMapper = objectMapper;
  }

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public PartyDTO getParty(final String sampleUnitType, final UUID partyId) {
    log.debug(
        "Retrieving party with sampleUnitType {} - partyId {}", sampleUnitType, partyId.toString());
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getPartySvc().getPartyBySampleUnitTypeAndIdPath(),
            null,
            sampleUnitType,
            partyId);

    return makePartyServiceRequest(uriComponents);
  }

  public PartyDTO getParty(final String sampleUnitType, final String partyId) {
    log.debug("entering party with sampleUnitType {} - partyId {}", sampleUnitType, partyId);
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getPartySvc().getPartyBySampleUnitTypeAndIdPath(),
            null,
            sampleUnitType,
            partyId);

    return makePartyServiceRequest(uriComponents);
  }

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public PartyDTO getPartyWithAssociationsFilteredBySurvey(
      final String sampleUnitType, final UUID partyId, final String surveyId) {
    log.debug(
        "Retrieving party with sampleUnitType {}"
            + " - partyId {}, with associations filtered by surveyId - {}",
        sampleUnitType,
        partyId.toString(),
        surveyId);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.put("survey_id", Collections.singletonList(surveyId));

    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getPartySvc().getPartyBySampleUnitTypeAndIdPath(),
            queryParams,
            sampleUnitType,
            partyId);

    return makePartyServiceRequest(uriComponents);
  }

  private PartyDTO makePartyServiceRequest(final UriComponents uriComponents) {
    final HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    final ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);

    PartyDTO result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      final String responseBody = responseEntity.getBody();
      try {
        result = objectMapper.readValue(responseBody, PartyDTO.class);
      } catch (final IOException e) {
        final String msg = String.format("cause = %s - message = %s", e.getCause(), e.getMessage());
        log.error(msg);
        log.error("Stacktrace: ", e);
      }
    }

    return result;
  }
}
