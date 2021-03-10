package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

public abstract class ActionProcessingService {
  private static final Logger log = LoggerFactory.getLogger(ActionProcessingService.class);

  public static final String ACTION_TYPE_NOT_DEFINED = "ActionType is not defined for action";
  public static final String DATE_FORMAT_IN_REMINDER_EMAIL = "dd/MM/yyyy";
  public static final String CANCELLATION_REASON = "Action cancelled by Response Management";
  public static final String ACTIVE = "ACTIVE";
  public static final String CREATED = "CREATED";
  public static final String ENABLED = "ENABLED";
  public static final String NOTIFY = "Notify";
  public static final String PENDING = "PENDING";
}
