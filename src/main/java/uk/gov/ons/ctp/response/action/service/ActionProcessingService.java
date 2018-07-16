package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.domain.model.Action;

/**
 * The service to go from Action to ActionRequests, ActionCancels. It then publishes them to
 * downstream services (handlers).
 *
 * <p>It enriches Actions with case, questionnaire, address, caseevent details, etc. It then updates
 * its own action table to change the action state to PENDING, posts a new CaseEvent to the Case
 * Service, and constructs an outbound ActionRequest/ActionCancel instance.
 */
public interface ActionProcessingService {

  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  public static final String CANCELLATION_REASON = "Action cancelled by Response Management";
  public static final String ENABLED = "ENABLED";
  public static final String PENDING = "PENDING";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";

  /**
   * To produce an ActionRequest and publish it to the relevant Handler.
   *
   * @param action the Action
   * @throws CTPException
   */
  void processActionRequest(Action action) throws CTPException;

  /**
   * To produce an ActionCancel and publish it to the relevant Handler.
   *
   * @param action the Action
   * @throws CTPException
   */
  void processActionCancel(Action action) throws CTPException;
}
