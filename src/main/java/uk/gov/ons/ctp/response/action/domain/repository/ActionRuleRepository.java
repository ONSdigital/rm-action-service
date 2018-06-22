package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
   * Return all action rules for the specified action plan id.
   *
   * @param actionPlanId This is the case id
   * @return List<ActionRule> This returns all action rules for the specified action plan id.
   */
  @Query(
      value =
          "select ar.actionrulepk, ar.actionplanfk, ar.actiontypefk, ar.name, ar.description,"
              + " ar.daysoffset, ar.priority, ar.id "
              + "from action.actionrule ar "
              + "inner join action.actionplan ap on ar.actionplanfk = ap.actionplanpk "
              + "where ap.id = :p_actionPlanId ;",
      nativeQuery = true)
  List<ActionRule> findByActionPlanId(@Param("p_actionPlanId") UUID actionPlanId);
}
