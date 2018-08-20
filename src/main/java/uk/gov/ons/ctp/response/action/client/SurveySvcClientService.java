package uk.gov.ons.ctp.response.action.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import uk.gov.ons.response.survey.representation.SurveyDTO;

/** Impl of the service that centralizes all REST calls to the Survey service */
@Slf4j
@Service
public class SurveySvcClientService {

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Autowired
  @Qualifier("surveySvcClient")
  private RestUtility restUtility;

  @Autowired private ObjectMapper objectMapper;

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public SurveyDTO requestDetailsForSurvey(final String surveyId) throws RestClientException {
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getSurveySvc().getRequestSurveyPath(), null, surveyId);

    final HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    final ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);

    SurveyDTO result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      final String responseBody = responseEntity.getBody();
      try {
        result = objectMapper.readValue(responseBody, SurveyDTO.class);
      } catch (final IOException e) {
        final String msg = String.format("cause = %s - message = %s", e.getCause(), e.getMessage());
        log.error(msg);
        log.error("Stacktrace: ", e);
      }
    }

    log.debug("made call to survey service and retrieved {}", result);
    return result;
  }
}