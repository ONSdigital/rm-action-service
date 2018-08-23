package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This bean will have the actionDistributor injected into it by spring on constructions. It will
 * then schedule the running of the distributor using details from the AppConfig
 */
@CoverageIgnore
@Component
public class DistributionScheduler {
  private static final Logger log = LoggerFactory.getLogger(DistributionScheduler.class);

  private ActionDistributor actionDistributorImpl;

  public DistributionScheduler(ActionDistributor actionDistributorImpl) {
    this.actionDistributorImpl = actionDistributorImpl;
  }

  /** Scheduled execution of the Action Distributor */
  @Scheduled(fixedDelayString = "#{appConfig.actionDistribution.delayMilliSeconds}")
  public void run() {
    DistributionInfo distInfo = actionDistributorImpl.distribute();
    log.with("count", distInfo.getInstructionCounts().get(0).getCount())
        .debug("Action requests created");
    log.with("count", distInfo.getInstructionCounts().get(0).getCount())
        .debug("Actions requests cancelled");
  }
}
