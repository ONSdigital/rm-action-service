package uk.gov.ons.ctp.response.action.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.service.CommsTemplateSvcClientService;
import uk.gov.ons.response.commstemplate.representation.CommsTemplateDTO;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Service
public class CommsTemplateSvcClientServiceImpl implements CommsTemplateSvcClientService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("commsTemplateSvcClient")
    private RestUtility restUtility;

    @Autowired
    private ObjectMapper objectMapper;

    @Cacheable("commsTemplate")
    @Retryable(value = {RestClientException.class}, maxAttemptsExpression = "#{${retries.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${retries.backoff}}"))
    @Override
    public CommsTemplateDTO getCommsTemplateByClassifiers(Map<String, String> classifiers) {
        UriComponents uriComponents = restUtility.createUriComponents(
                appConfig.getCommsTemplateSvc().getTemplateByClassifiersGetPath(), (MultiValueMap)classifiers); // TODO: Check if this casting is ok?

        HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET,
                httpEntity, String.class);

        CommsTemplateDTO result = null;
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            try {
                result = objectMapper.readValue(responseBody, CommsTemplateDTO.class);
            } catch (IOException e) {
                String msg = String.format("cause = %s - message = %s", e.getCause(), e.getMessage());
                log.error(msg);
                log.error("Stacktrace: ", e);
            }

            log.info("Made call to Comms Template Service and retrieved {}", result);
        }
        return result;
    }
}
