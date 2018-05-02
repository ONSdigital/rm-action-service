package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRuleRepository;
import uk.gov.ons.ctp.response.action.service.ActionRuleService;

import java.util.List;
import java.util.UUID;


/**
 * Implementation
 */
@Service
@Slf4j
public class ActionRuleServiceImpl implements ActionRuleService {

  private static final int TRANSACTION_TIMEOUT = 30;

  @Autowired
  private ActionRuleRepository actionRuleRepo;

  @CoverageIgnore
  @Override
  public List<ActionRule> findActionRulesByActionPlanId(final UUID actionPlanId) {
    log.debug("Entering findActionRulesByActionPlanId");
    return actionRuleRepo.findByActionPlanId(actionPlanId);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, timeout = TRANSACTION_TIMEOUT)
  public ActionRule createActionRule(final ActionRule actionRule) {

    // guard against the caller providing an id - we would perform an update otherwise
    actionRule.setActionRulePK(null);

    actionRule.setId(UUID.randomUUID());
    return actionRuleRepo.saveAndFlush(actionRule);
  }
}
