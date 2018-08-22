package uk.gov.ons.ctp.response.action.scheduled.distribution;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This bean will have the actionDistributor injected into it by spring on constructions. It will
 * then schedule the running of the distributor using details from the AppConfig
 */
@CoverageIgnore
@Component
@Slf4j
public class DistributionScheduler {

  private ActionDistributor actionDistributorImpl;

  public DistributionScheduler(ActionDistributor actionDistributorImpl) {
    this.actionDistributorImpl = actionDistributorImpl;
  }

  /** Scheduled execution of the Action Distributor */
  @Scheduled(fixedDelayString = "#{appConfig.actionDistribution.delayMilliSeconds}")
  public void run() {
    try {
      actionDistributorImpl.distribute();
    } catch (final Exception e) {
      log.error("Exception in action distributor", e);
    }
  }
}
