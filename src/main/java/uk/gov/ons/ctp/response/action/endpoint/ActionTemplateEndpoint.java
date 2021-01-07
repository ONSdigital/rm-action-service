package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import javax.validation.Valid;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;

@RestController
@RequestMapping(value = "/action-template", produces = "application/json")
public class ActionTemplateEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionTemplateEndpoint.class);
  private ActionTemplateRepository actionTemplateRepo;
  private MapperFacade mapperFacade;

  @Autowired
  public ActionTemplateEndpoint(
      ActionTemplateRepository actionTemplate, MapperFacade mapperFacade) {
    this.actionTemplateRepo = actionTemplate;
    this.mapperFacade = mapperFacade;
  }

  /**
   * Endpoint to register a new action template
   *
   * @param requestActionTemplate represents the new template
   * @return ResponseEntity
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ActionTemplateDTO> createActionTemplate(
      @RequestBody @Valid final ActionTemplateDTO requestActionTemplate) {
    log.with("Template Name", requestActionTemplate.getType())
        .with("Description", requestActionTemplate.getDescription())
        .debug("Recording a new action template");
    ActionTemplate newTemplate = mapperFacade.map(requestActionTemplate, ActionTemplate.class);
    ActionTemplate createdTemplate = actionTemplateRepo.save(newTemplate);
    ActionTemplateDTO mappedCreatedActionTemplateDTO =
        mapperFacade.map(createdTemplate, ActionTemplateDTO.class);
    String newResourceUrl =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(mappedCreatedActionTemplateDTO.getType())
            .toUri()
            .toString();
    return ResponseEntity.created(URI.create(newResourceUrl)).body(mappedCreatedActionTemplateDTO);
  }
}
