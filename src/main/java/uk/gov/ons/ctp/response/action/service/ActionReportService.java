package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.domain.repository.ActionReportRepository;

/** Create report via stored procedure */
@Service
public class ActionReportService {
  private static final Logger log = LoggerFactory.getLogger(ActionReportService.class);

  @Autowired private ActionReportRepository actionReportRepository;

  public void createReport() {
    log.debug("Entering createReport...");
    final boolean reportResult = actionReportRepository.miStoredProcedure();
    log.with("report", reportResult).debug("Just ran the mi report");
  }
}
