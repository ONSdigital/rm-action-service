package uk.gov.ons.ctp.response.action.scheduled.distribution;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This bean will have the actionDistributor injected into it by spring on constructions. It will
 * then schedule the running of the distributor using details from the AppConfig
 */
@CoverageIgnore
@Component
public class DistributionScheduler implements HealthIndicator {
  private static final Logger log = LoggerFactory.getLogger(DistributionScheduler.class);

  private DistributionInfo distributionInfo = new DistributionInfo();

  @Autowired private ActionDistributor actionDistributorImpl;

  @Override
  public Health health() {
    return Health.up().withDetail("distributionInfo", distributionInfo).build();
  }

  /** Scheduled execution of the Action Distributor */
  @Scheduled(fixedDelayString = "#{appConfig.actionDistribution.delayMilliSeconds}")
  public void run() {
    try {
      distributionInfo = actionDistributorImpl.distribute();
    } catch (final Exception e) {
      log.error("Exception in action distributor", e);
    }
  }
}
