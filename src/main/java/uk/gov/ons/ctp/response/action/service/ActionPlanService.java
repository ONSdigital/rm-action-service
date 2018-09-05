package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;

/** Implementation */
@Service
public class ActionPlanService {
  private static final Logger log = LoggerFactory.getLogger(ActionPlanService.class);

  private static final int TRANSACTION_TIMEOUT = 30;

  private ActionPlanRepository actionPlanRepo;

  @Autowired
  public ActionPlanService(final ActionPlanRepository actionPlanRepo) {
    this.actionPlanRepo = actionPlanRepo;
  }

  @CoverageIgnore
  public List<ActionPlan> findActionPlans() {
    log.debug("Entering findActionPlans");
    return this.actionPlanRepo.findAll();
  }

  @CoverageIgnore
  public List<ActionPlan> findActionPlansBySelectors(final HashMap<String, String> selectors) {
    log.with(selectors).debug("Finding action plans by selectors");
    return this.actionPlanRepo.findBySelectorsIn(selectors);
  }

  @CoverageIgnore
  public ActionPlan findActionPlan(final Integer actionPlanKey) {
    log.with("action_plan_pk", actionPlanKey).debug("Entering findActionPlan with primary key");
    return this.actionPlanRepo.findOne(actionPlanKey);
  }

  @CoverageIgnore
  public ActionPlan findActionPlanById(final UUID actionPlanId) {
    log.with("action_plan_id", actionPlanId).debug("Entering findActionPlanById with id");
    return this.actionPlanRepo.findById(actionPlanId);
  }

  @CoverageIgnore
  public ActionPlan findActionPlanByName(final String name) {
    log.with("action_plan_name", name).debug("Entering findActionPlanByName with name");
    return this.actionPlanRepo.findByName(name);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public ActionPlan createActionPlan(final ActionPlan actionPlan) {
    log.with("selectors", actionPlan.getSelectors())
        .with("action_plan_name", actionPlan.getName())
        .debug("Creating action plan");

    ActionPlan savedActionPlan = saveActionPlan(actionPlan);

    log.with("selectors", actionPlan.getSelectors())
        .with("action_plan_name", actionPlan.getName())
        .with("action_plan_id", actionPlan.getId())
        .debug("Successfully created action plan");
    return savedActionPlan;
  }

  private ActionPlan saveActionPlan(final ActionPlan actionPlan) {
    actionPlan.setActionPlanPK(null);
    actionPlan.setId(UUID.randomUUID());
    return this.actionPlanRepo.saveAndFlush(actionPlan);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  public ActionPlan updateActionPlan(final UUID actionPlanId, final ActionPlan actionPlan) {
    log.with("action_plan_id", actionPlanId).debug("Updating action plan");
    ActionPlan existingActionPlan = this.actionPlanRepo.findById(actionPlanId);
    if (existingActionPlan != null) {
      boolean needsUpdate = false;

      final String newDescription = actionPlan.getDescription();
      if (newDescription != null) {
        needsUpdate = true;
        existingActionPlan.setDescription(newDescription);
      }

      final Date newLastRunDateTime = actionPlan.getLastRunDateTime();
      if (newLastRunDateTime != null) {
        needsUpdate = true;
        existingActionPlan.setLastRunDateTime(new Timestamp(newLastRunDateTime.getTime()));
      }

      final HashMap<String, String> newSelectors = actionPlan.getSelectors();
      if (newSelectors != null) {
        needsUpdate = true;
        existingActionPlan.setSelectors(newSelectors);
      }

      if (needsUpdate) {
        existingActionPlan = this.actionPlanRepo.saveAndFlush(existingActionPlan);
      }
    }
    return existingActionPlan;
  }
}
