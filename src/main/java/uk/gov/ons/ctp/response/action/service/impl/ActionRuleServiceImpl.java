package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


  @Autowired
  private ActionRuleRepository actionRuleRepo;

  @CoverageIgnore
  @Override
  public List<ActionRule> findActionRulesByActionPlanId(final UUID actionPlanId) {
    log.debug("Entering findActionRulesByActionPlanId");
    return actionRuleRepo.findByActionPlanId(actionPlanId);
  }
}
