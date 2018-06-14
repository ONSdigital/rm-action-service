package uk.gov.ons.ctp.response.action.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanSelector;

import java.util.List;

/**
 * JPA data repository for action plan selectors
 */
@Repository
public interface ActionPlanSelectorRepository extends JpaRepository<ActionPlanSelector, Integer> {

  /**
   * Return ActionPlan for the specified action plan id.
   *
   * @return List of all ActionPlanSelectors
   */
  List<ActionPlanSelector> findAll();

}
