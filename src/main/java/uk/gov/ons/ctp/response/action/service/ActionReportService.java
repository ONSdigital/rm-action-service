package uk.gov.ons.ctp.response.action.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.repository.ActionReportRepository;

/** Create report via stored procedure */
@Service
@Slf4j
public class ActionReportService {

  @Autowired private ActionReportRepository actionReportRepository;

  public void createReport() {
    log.debug("Entering createReport...");
    final boolean reportResult = actionReportRepository.miStoredProcedure();
    log.debug("Just ran the mi report and result is {}", reportResult);
  }
}
