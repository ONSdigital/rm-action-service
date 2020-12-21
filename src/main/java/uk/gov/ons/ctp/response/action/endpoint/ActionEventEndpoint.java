package uk.gov.ons.ctp.response.action.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.response.action.representation.events.Event;
import uk.gov.ons.ctp.response.action.service.ProcessEventService;

@RestController
@RequestMapping(value = "/process-event", produces = "application/json")
public class ActionEventEndpoint {
  private static final Logger log = LoggerFactory.getLogger(ActionEventEndpoint.class);
  private final ProcessEventService processEventService;

  @Autowired
  public ActionEventEndpoint(ProcessEventService processEventService) {
    this.processEventService = processEventService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity processEvents(@RequestBody @Valid Event event) {
    log.with("collectionExercise", event.getCollectionExerciseID())
        .with("EventTag", event.getTag())
        .info("Processing Event");
    processEventService.processEvents(event.getCollectionExerciseID(), event.getTag().toString());
    return ResponseEntity.accepted().body(null);
  }
}
