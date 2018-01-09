package uk.gov.ons.ctp.response.action.service;

import org.springframework.web.client.RestClientException;
import uk.gov.ons.response.survey.representation.SurveyClassifierDTO;
import uk.gov.ons.response.survey.representation.SurveyClassifierTypeDTO;
import uk.gov.ons.response.survey.representation.SurveyDTO;

import java.util.List;

/**
 * Service responsible for making client calls to the Survey service
 */
public interface SurveySvcClientService {

  /**
   * Get survey details by survey UUID.
   *
   * @param surveyId UUID String for which to request details.
   * @return the survey details.
   * @throws RestClientException something went wrong making http call.
   *
   */
  SurveyDTO getDetailsForSurvey(String surveyId) throws RestClientException;

  List<SurveyClassifierDTO> getSurveyClassifierTypes(String surveyId) throws RestClientException;

  SurveyClassifierTypeDTO getSurveyClassifiers(String surveyId, String classifierTypeId) throws RestClientException;
}
