package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.response.commstemplate.representation.CommsTemplateDTO;

import java.util.Map;

/**
 * A Service which utilises the CommsTemplateSvc via RESTful client calls
 *
 */
public interface CommsTemplateSvcClientService {
    /**
     * Find CollectionExerciseDTO entity by specified classifiers
     *
     * @param classifiers, map of classifiers
     * @return  CommsTemplateDTO
     * */
    CommsTemplateDTO getCommsTemplateByClassifiers(Map<String, String> classifiers);
}