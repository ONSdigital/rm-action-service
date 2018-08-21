package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRuleRepository;

/** Implementation */
@Service
public class ActionRuleService {
  private static final Logger log = LoggerFactory.getLogger(ActionRuleService.class);

  private static final int TRANSACTION_TIMEOUT = 30;

  @Autowired private ActionRuleRepository actionRuleRepo;

  @CoverageIgnore
  public List<ActionRule> findActionRulesByActionPlanFK(final Integer actionPlanFK) {
    log.debug("Entering findActionRulesByActionPlanFK");
    return actionRuleRepo.findByActionPlanFK(actionPlanFK);
  }

  @Transactional(propagation = Propagation.REQUIRED, timeout = TRANSACTION_TIMEOUT)
  public ActionRule createActionRule(final ActionRule actionRule) {

    // guard against the caller providing an id - we would perform an update otherwise
    actionRule.setActionRulePK(null);

    actionRule.setId(UUID.randomUUID());
    return actionRuleRepo.saveAndFlush(actionRule);
  }

  @Transactional(propagation = Propagation.REQUIRED, timeout = TRANSACTION_TIMEOUT)
  public ActionRule updateActionRule(final ActionRule actionRule) {
    final UUID actionRuleId = actionRule.getId();

    ActionRule existingActionRule = actionRuleRepo.findById(actionRuleId);
    if (existingActionRule == null) {
      return null;
    }

    boolean needsUpdate = false;

    final Integer newPriority = actionRule.getPriority();
    if (newPriority != null) {
      needsUpdate = true;
      existingActionRule.setPriority(newPriority);
    }

    final String newName = actionRule.getName();
    if (newName != null) {
      needsUpdate = true;
      existingActionRule.setName(newName);
    }

    final String newDescription = actionRule.getDescription();
    if (newDescription != null) {
      needsUpdate = true;
      existingActionRule.setDescription(newDescription);
    }

    final OffsetDateTime triggerDateTime = actionRule.getTriggerDateTime();
    if (triggerDateTime != null) {
      needsUpdate = true;
      existingActionRule.setTriggerDateTime(triggerDateTime);
    }

    if (needsUpdate) {
      log.debug("updating action with {}", existingActionRule);
      existingActionRule = actionRuleRepo.saveAndFlush(existingActionRule);
    }

    return existingActionRule;
  }
}
