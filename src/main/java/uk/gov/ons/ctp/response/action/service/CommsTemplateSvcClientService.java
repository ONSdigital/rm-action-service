package uk.gov.ons.ctp.response.action.service;

import org.springframework.util.MultiValueMap;
import uk.gov.ons.response.commstemplate.representation.CommsTemplateDTO;

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
    CommsTemplateDTO getCommsTemplate(MultiValueMap<String, String> classifiers);
}