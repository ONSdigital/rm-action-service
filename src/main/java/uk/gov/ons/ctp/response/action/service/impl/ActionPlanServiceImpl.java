package uk.gov.ons.ctp.response.action.service.impl;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanSelector;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanSelectorRepository;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementation
 */
@Service
@Slf4j
public class ActionPlanServiceImpl implements ActionPlanService {

  private static final int TRANSACTION_TIMEOUT = 30;

  private ActionPlanRepository actionPlanRepo;

  private ActionPlanSelectorRepository actionPlanSelectorRepository;

  private MapperFacade mapperFacade;

  @Autowired
  public ActionPlanServiceImpl(final ActionPlanRepository actionPlanRepo,
                               final ActionPlanSelectorRepository actionPlanSelectorRepository,
                               final MapperFacade mapperFacade) {
    this.actionPlanRepo = actionPlanRepo;
    this.actionPlanSelectorRepository = actionPlanSelectorRepository;
    this.mapperFacade = mapperFacade;
  }


  @CoverageIgnore
  @Override
  public List<ActionPlan> findActionPlans() {
    log.debug("Entering findActionPlans");
    return this.actionPlanRepo.findAll();
  }

  @CoverageIgnore
  @Override
  public ActionPlan findActionPlan(final Integer actionPlanKey) {
    log.debug("Entering findActionPlan with primary key {}", actionPlanKey);
    return this.actionPlanRepo.findOne(actionPlanKey);
  }

  @CoverageIgnore
  @Override
  public ActionPlan findActionPlanById(final UUID actionPlanId) {
    log.debug("Entering findActionPlanById with id {}", actionPlanId);
    return this.actionPlanRepo.findById(actionPlanId);
  }

  @CoverageIgnore
  @Override
  public ActionPlan findActionPlanByName(final String name) {
    log.debug("Entering findActionPlanByName with name {}", name);
    return this.actionPlanRepo.findByName(name);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  public ActionPlanDTO createActionPlan(final ActionPlan actionPlan, final ActionPlanSelector actionPlanSelector) {
    log.debug("Creating action plan, Name: {}, Selectors: {}",
            actionPlan.getName(), actionPlanSelector.getSelectors());

    ActionPlan savedActionPlan = saveActionPlan(actionPlan);
    ActionPlanDTO actionPlanDTO = mapperFacade.map(savedActionPlan, ActionPlanDTO.class);

    // If selectors are provided create a row in the ActionPlanSelectors column
    if (actionPlanSelector.getSelectors() != null) {
      ActionPlanSelector savedActionPlanSelectors = saveActionPlanSelectors(savedActionPlan, actionPlanSelector);
      actionPlanDTO.setSelectors(savedActionPlanSelectors.getSelectors());
    }

    log.debug("Successfully created action plan, Name: {}, ActionPlanId: {}, Selectors: {}",
            actionPlan.getName(), actionPlan.getId(), actionPlanSelector.getSelectors());
    return actionPlanDTO;
  }

  private ActionPlan saveActionPlan(final ActionPlan actionPlan) {
    actionPlan.setActionPlanPK(null);
    actionPlan.setId(UUID.randomUUID());
    return this.actionPlanRepo.saveAndFlush(actionPlan);
  }

  private ActionPlanSelector saveActionPlanSelectors(final ActionPlan actionPlan, final ActionPlanSelector actionPlanSelector) {
    actionPlanSelector.setActionPlanFk(actionPlan.getActionPlanPK());
    return this.actionPlanSelectorRepository.saveAndFlush(actionPlanSelector);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  public ActionPlan updateActionPlan(final UUID actionPlanId, final ActionPlan actionPlan) {
    log.debug("Entering updateActionPlan with id {}", actionPlanId);
    ActionPlan existingActionPlan = this.actionPlanRepo.findById(actionPlanId);
    if (existingActionPlan != null) {
      boolean needsUpdate = false;

      final String newDescription = actionPlan.getDescription();
      log.debug("newDescription = {}", newDescription);
      if (newDescription != null) {
        needsUpdate = true;
        existingActionPlan.setDescription(newDescription);
      }

      final Date newLastRunDateTime = actionPlan.getLastRunDateTime();
      log.debug("newLastRunDatetime = {}", newLastRunDateTime);
      if (newLastRunDateTime != null) {
        needsUpdate = true;
        existingActionPlan.setLastRunDateTime(new Timestamp(newLastRunDateTime.getTime()));
      }

      if (needsUpdate) {
        log.debug("about to update the action plan with id {}", actionPlanId);
        existingActionPlan = this.actionPlanRepo.save(existingActionPlan);
      }
    }
    return existingActionPlan;
  }
}
