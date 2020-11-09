package uk.gov.ons.ctp.response.action.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.messaging.handler.annotation.Header;
import uk.gov.ons.ctp.response.action.message.instruction.Action;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.ActionExportService;

/** This class is used to publish ActionInstructions to the downstream handlers. */
@MessageEndpoint
public class ActionInstructionPublisher {
  private static final Logger log = LoggerFactory.getLogger(ActionInstructionPublisher.class);

  public static final String ACTION = "Action.";
  public static final String BINDING = ".binding";

  @Qualifier("actionInstructionRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired private ActionExportService actionExportService;

  public void sendActionInstruction(@Header("HANDLER") final String handler, final Action action) {
    log.with("action_id", action.getActionId())
        .with("handler", handler)
        .info("Sending action instruction");

    final ActionInstruction instruction = new ActionInstruction();
    if (action instanceof ActionRequest) {
      instruction.setActionRequest((ActionRequest) action);
    } else if (action instanceof ActionCancel) {
      instruction.setActionCancel((ActionCancel) action);
    }

    if ("PRINTER".equalsIgnoreCase(handler)) {
      actionExportService.acceptInstruction(instruction);
    } else {
      final String routingKey = String.format("%s%s%s", ACTION, handler, BINDING);
      rabbitTemplate.convertAndSend(routingKey, instruction);
    }
  }
}
