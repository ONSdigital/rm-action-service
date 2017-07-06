package uk.gov.ons.ctp.response.action.message.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.messaging.handler.annotation.Header;
import uk.gov.ons.ctp.response.action.message.InstructionPublisher;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

import java.util.List;

/**
 * This class is used to publish a list of action request objects to the
 * downstream handlers The sendRequests method is called by the
 * ActionDistibutor, and on return the ActionInstruction object constructed is
 * sent to the instructionOutbound SpringIntegration channel (see the
 * integration-context.xml for details of the channel and the outbound flow)
 *
 */
@MessageEndpoint
public class InstructionPublisherImpl implements InstructionPublisher {

  @Qualifier("actionInstructionRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  private static final String ACTION = "Action.";
  private static final String BINDING = ".binding";

  /**
   * The implementation will be responsible for publishing ActionRequests to the
   * SpringIntegration outbound flow
   *
   * @param handler the handler that the outbound flow should send to - taken
   *          directly from the Actions ActionType
   * @param actionRequests the requests to publish
   * @param actionCancels the cancels to publish
   */
  public void sendInstructions(@Header("HANDLER") String handler, List<ActionRequest> actionRequests,
      List<ActionCancel> actionCancels) {
    String routingKey = String.format("%s%s%s", ACTION, handler, BINDING);

    if (actionRequests != null && !actionRequests.isEmpty()) {
      actionRequests.forEach(actionRequest -> {
        ActionInstruction instruction = new ActionInstruction();
        instruction.setActionRequest(actionRequest);
        rabbitTemplate.convertAndSend(routingKey, instruction);
      });
    }

    if (actionCancels != null && !actionCancels.isEmpty()) {
      actionCancels.forEach(actionCancel -> {
        ActionInstruction instruction = new ActionInstruction();
        instruction.setActionCancel(actionCancel);
        rabbitTemplate.convertAndSend(routingKey, instruction);
      });
    }
  }
}
