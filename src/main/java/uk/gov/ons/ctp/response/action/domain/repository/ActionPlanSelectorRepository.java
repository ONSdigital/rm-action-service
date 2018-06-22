package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanSelector;

/** JPA data repository for action plan selectors */
@Repository
public interface ActionPlanSelectorRepository extends JpaRepository<ActionPlanSelector, Integer> {

  List<ActionPlanSelector> findAll();

  /**
   * Return ActionPlanSelectors for the specified action plan fk
   *
   * @return Matching action plan selector if it exists
   */
  ActionPlanSelector findFirstByActionPlanFk(Integer actionPlanFk);
}
