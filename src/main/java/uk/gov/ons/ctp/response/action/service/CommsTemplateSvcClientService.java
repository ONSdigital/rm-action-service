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
     * @param classifiers hash map of classifiers
     * TODO: create CommsTemplateDTO
     * @return  CommsTemplateDTO Returns the CommsTemplateDTO for the specified classifiers.
     * */
    CommsTemplateDTO getCommsTemplateByClassifiers(Map<String, String> classifiers);
}