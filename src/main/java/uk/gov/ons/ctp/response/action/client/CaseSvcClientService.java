package uk.gov.ons.ctp.response.action.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseIACDTO;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;

/** Impl of the service that centralizes all REST calls to the Case service */
@Service
public class CaseSvcClientService {
  private static final Logger log = LoggerFactory.getLogger(CaseSvcClientService.class);

  @Autowired private AppConfig appConfig;

  @Autowired private RestTemplate restTemplate;

  @Autowired
  @Qualifier("caseSvcClient")
  private RestUtility restUtility;

  @Autowired private ObjectMapper objectMapper;

  @Retryable(
      value = {RestClientException.class},
      maxAttemptsExpression = "#{${retries.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
  public CaseDetailsDTO getCaseWithIACandCaseEvents(final UUID caseId) {
    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("iac", "true");
    queryParams.add("caseevents", "true");
    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCaseSvc().getCaseByCaseGetPath(), queryParams, caseId);

    final HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    final ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, String.class);

    CaseDetailsDTO result = null;
    if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
      final String responseBody = responseEntity.getBody();
      try {
        result = objectMapper.readValue(responseBody, CaseDetailsDTO.class);
      } catch (final IOException e) {
        log.with("caseId", caseId).error("Unable to read case details response", e);
      }
    }
    return result;
  }

  /**
   * Generate a new IAC for specified case
   *
   * @param caseId Case you want to update with a new generated IAC
   * @return The new IAC
   */
  public CaseIACDTO generateNewIacForCase(final UUID caseId) {

    final UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCaseSvc().getGenerateNewIacForCasePostPath(), null, caseId);

    final HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

    final ResponseEntity<CaseIACDTO> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, httpEntity, CaseIACDTO.class);

    return responseEntity.getBody();
  }
}
