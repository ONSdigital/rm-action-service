package uk.gov.ons.ctp.response.action.domain.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;

/** JPA Data Repository for ActionCase which is backed by action.case table */
@Repository
public interface ActionCaseRepository extends JpaRepository<ActionCase, Integer> {

  /**
   * find cases (by virtue open) for actionplanid
   *
   * @param actionPlanId the action plan
   * @return the list of (open) cases assoc with that plan
   */
  List<ActionCase> findByActionPlanId(Integer actionPlanId);

  /**
   * find a case by its id - the uuid which is not the primary key btw
   *
   * @param caseId the UUID of the case to retrieve
   * @return the case
   */
  ActionCase findById(UUID caseId);

  /**
   * just count cases for an actionplan
   *
   * @param actionPlanKey the plan id
   * @return how many cases for that plan
   */
  Long countByActionPlanFK(Integer actionPlanKey);

  /**
   * @param actionPlanId Action Plan primary key filter criteria
   * @return Return all true if case exists with active action plan
   */
  @Query(
      value =
          "SELECT EXISTS (SELECT 1 "
              + "FROM action.case c, action.actionrule r "
              + "WHERE c.actionplanstartdate <= :currentTime "
              + "AND c.actionplanenddate >= :currentTime "
              + "AND r.daysoffset <= EXTRACT(DAY FROM (:currentTime - c.actionplanstartdate)) "
              + "AND c.actionplanFk = :actionPlanId "
              + "AND r.actionplanFK = c.actionplanFK)",
      nativeQuery = true)
  boolean hasActiveCaseWithActionPlanId(
      @Param("actionPlanId") Integer actionPlanId, @Param("currentTime") Timestamp currentTime);
}
