package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;

/** JPA Data Repository. */
@Repository
public interface ActionPlanRepository extends JpaRepository<ActionPlan, Integer> {

  /**
   * Return ActionPlan for the specified action plan id.
   *
   * @param id UUID id for ActionPlan
   * @return ActionPlan returns ActionPlan for associated id
   */
  ActionPlan findById(UUID id);

  /**
   * Return ActionPlan for the specified action plan name.
   *
   * @param name UUID id for ActionPlan
   * @return ActionPlan returns ActionPlan for associated id
   */
  ActionPlan findByName(String name);
}
