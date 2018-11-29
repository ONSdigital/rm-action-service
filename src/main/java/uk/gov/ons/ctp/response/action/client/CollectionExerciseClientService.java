package uk.gov.ons.ctp.response.action.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
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
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/** Impl of the service that centralizes all REST calls to the Collection Exercise service */
@Service
public class CollectionExerciseClientService {
  private static final Logger log = LoggerFactory.getLogger(CollectionExerciseClientService.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Autowired
  @Qualifier("collectionExerciseSvcClient")
  private RestUtility restUtility;

  @Autowired private ObjectMapper objectMapper;

  @Cacheable("collectionexercises")
  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public CollectionExerciseDTO getCollectionExercise(final UUID collectionExcerciseId) {
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCollectionExerciseSvc().getCollectionByCollectionExerciseGetPath(),
            null,
            collectionExcerciseId);

    final HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    log.with("collection_exericse_id", collectionExcerciseId)
        .with("uri", uriComponents.toUri())
        .debug("Retrieving collection exercise");

    final ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);

    CollectionExerciseDTO result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      final String responseBody = responseEntity.getBody();
      try {
        result = objectMapper.readValue(responseBody, CollectionExerciseDTO.class);
      } catch (final IOException e) {
        log.error("Unable to read collection exercise response", e);
      }
    }

    log.with("collection_exercise", result).debug("made call to collection Exercise");
    return result;
  }
}
