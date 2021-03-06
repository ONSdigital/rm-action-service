package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;

/** JPA Data Repository for ActionCase which is backed by action.case table */
@Repository
public interface ActionCaseRepository extends JpaRepository<ActionCase, Integer> {

  /**
   * find cases (by virtue open) for actionplanid
   *
   * @param actionPlanFK the action plan
   * @return the list of (open) cases assoc with that plan
   */
  List<ActionCase> findByActionPlanFK(Integer actionPlanFK);

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
   * @return boolean for if cases exist for given action plan
   */
  boolean existsByActionPlanFK(Integer actionPlanKey);

  List<ActionCase> findByCollectionExerciseIdAndActiveEnrolment(
      UUID collectionExerciseId, boolean activeEnrolment);

  List<ActionCase> findByIdIn(List<UUID> caseId);

  Long countByCollectionExerciseId(UUID collectionExerciseId);
}
