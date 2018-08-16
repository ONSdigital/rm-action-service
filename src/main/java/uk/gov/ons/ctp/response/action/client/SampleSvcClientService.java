package uk.gov.ons.ctp.response.action.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
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
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;

@Slf4j
@Service
public class SampleSvcClientService {

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Autowired
  @Qualifier("sampleSvcClient")
  private RestUtility restUtility;

  @Autowired private ObjectMapper objectMapper;

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public SampleAttributesDTO getSampleAttributes(UUID sampleUnitId) {
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getSampleSvc().getSampleAttributesById(), null, sampleUnitId);

    final HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    final ResponseEntity<SampleAttributesDTO> responseEntity =
        restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.GET, httpEntity, SampleAttributesDTO.class);

    return responseEntity.getBody();
  }
}
