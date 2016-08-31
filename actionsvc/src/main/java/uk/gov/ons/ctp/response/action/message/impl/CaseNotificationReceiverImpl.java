package uk.gov.ons.ctp.response.action.message.impl;

import javax.inject.Inject;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.CaseNotificationReceiver;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotifications;

/**
 * Message end point for Case notification life cycle messages, please see
 * flows-int.xml.
 *
 */
@MessageEndpoint
@Slf4j
public class CaseNotificationReceiverImpl implements CaseNotificationReceiver {

  @Inject
  private CaseNotificationService caseNotificationService;

  @Override
  @ServiceActivator(inputChannel = "notificationTransformed")
  public void acceptNotification(CaseNotifications caseNotifications) {
    log.debug("Entering acceptNotifications for  {} events", caseNotifications.getCaseNotifications().size());
    caseNotificationService.acceptNotification(caseNotifications.getCaseNotifications());
  }

}