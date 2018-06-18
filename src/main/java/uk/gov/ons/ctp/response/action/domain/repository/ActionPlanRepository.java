package uk.gov.ons.ctp.response.action.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;

import java.util.UUID;

/**
 * JPA data repository for action plans
 */
@Repository
public interface ActionPlanRepository extends JpaRepository<ActionPlan, Integer> {

  /**
   * Return ActionPlan for the specified action plan id.
   *
   * @param id UUID id for ActionPlan
   * @return ActionPlan returns ActionPlan for associated id
   */
  ActionPlan findById(UUID id);

  ActionPlan findFirstByActionPlanPK(Integer actionPlanPK);

  /**
   * Return ActionPlan for the specified action plan name.
   *
   * @param name UUID id for ActionPlan
   * @return ActionPlan returns ActionPlan for associated id
   */
  ActionPlan findByName(String name);

}
