package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTemplateRepository;

@RestController
@RequestMapping(value = "/action-template", produces = "application/json")
public class ActionTemplateEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionTemplateEndpoint.class);
  @Autowired private ActionTemplateRepository actionTemplateRepo;

  @Autowired
  public ActionTemplateEndpoint(ActionTemplateRepository actionTemplate) {
    this.actionTemplateRepo = actionTemplate;
  }

  /**
   * Endpoint to register a new action template
   *
   * @param requestActionTemplate represents the new template
   * @return ResponseEntity
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ActionTemplate> createActionTemplate(
      @RequestBody @Valid final ActionTemplate requestActionTemplate) {
    log.with("Template Name", requestActionTemplate.getType())
        .with("Description", requestActionTemplate.getDescription())
        .debug("Recording a new action template");
    ActionTemplate createdTemplate = actionTemplateRepo.save(requestActionTemplate);
    return ResponseEntity.created(
            URI.create(String.format("/template/%s", createdTemplate.getType())))
        .body(createdTemplate);
  }
}
