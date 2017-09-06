package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.response.survey.representation.SurveyDTO;

/**
 * Impl of the service that centralizes all REST calls to the Survey service
 */
@Slf4j
@Service
public class SurveySvcClientServiceImpl implements SurveySvcClientService {

  @Autowired
  private AppConfig appConfig;

  @Autowired
  @Qualifier("surveySvcClient")
  private RestClient surveySvcClient;

  @Override
  public SurveyDTO requestDetailsForSurvey(String surveyId) throws RestClientException {
    SurveyDTO surveyDTO = surveySvcClient.getResource(appConfig.getSurveySvc().getRequestSurveyPath(), SurveyDTO.class,
        surveyId);
    log.debug("made call to survey service and retrieved {}", surveyDTO);
    return surveyDTO;
  }
}
