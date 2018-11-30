package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;

/** JPA data repository for action plans */
@Repository
public interface ActionPlanRepository extends JpaRepository<ActionPlan, Integer> {

  /**
   * Return ActionPlan for the specified action plan id.
   *
   * @param id UUID id for ActionPlan
   * @return ActionPlan returns ActionPlan for associated id
   */
  @Cacheable("moreActionPlans")
  ActionPlan findById(UUID id);

  /**
   * Return ActionPlan for the specified ActionPlan primary key
   *
   * @param actionPlanPK ActionPlan primary key
   * @return ActionPlan returns ActionPlan for associated id
   */
  @Cacheable("actionPlans")
  ActionPlan findByActionPlanPK(Integer actionPlanPK);

  /**
   * Return ActionPlan for the specified action plan name.
   *
   * @param name UUID id for ActionPlan
   * @return ActionPlan returns ActionPlan for associated id
   */
  ActionPlan findByName(String name);

  /**
   * Return ActionPlan for the specified action plan name.
   *
   * @param selectors HashMap of selectors
   * @return returns list of ActionPlans which match selectors
   */
  List<ActionPlan> findBySelectorsIn(HashMap<String, String> selectors);
}
