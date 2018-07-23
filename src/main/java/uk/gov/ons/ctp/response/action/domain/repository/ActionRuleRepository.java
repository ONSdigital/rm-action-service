package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;

/** JPA Data Repository. */
@Repository
public interface ActionRuleRepository extends JpaRepository<ActionRule, Integer> {

  /**
   * Find action by UUID
   *
   * @param actionRuleId the action uuid
   * @return the ActionRule found
   */
  ActionRule findById(UUID actionRuleId);

  /**
   * Return all action rules for the specified action plan foreign key.
   *
   * @param actionPlanFK Action plan foreign key
   * @return List<ActionRule> This returns all action rules for the specified action plan.
   */
  List<ActionRule> findByActionPlanFK(Integer actionPlanFK);
}
