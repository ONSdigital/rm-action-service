package uk.gov.ons.ctp.response.action.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

/**
 * JPA Data Repository.
 */
@Repository
public interface ActionRepository extends JpaRepository<Action, BigInteger> {

  /**
   * Return all actions, most recent first.
   *
   * @return all actions
   */
  List<Action> findAllByOrderByCreatedDateTimeDesc();

  /**
   * Find action by UUID
   *
   * @param actionId the action uuid
   * @return the action found
   */
  Action findById(UUID actionId);

  /**
   * Return all actions for the specified case id.
   *
   * @param caseId This is the case id
   * @return List<Action> This returns all actions for the specified case id.
   */
  List<Action> findByCaseId(UUID caseId);

  /**
   * Return all actions for the specified case id, ordered by created DateTime.
   *
   * @param caseId This is the case id
   * @return List<Action> This returns all actions for the specified case id.
   */
  List<Action> findByCaseIdOrderByCreatedDateTimeDesc(UUID caseId);

  /**
   * Return all actions for the specified actionTypeName and state in created
   * date time order descending.
   *
   * @param actionTypeName ActionTypeName filter criteria
   * @param state          State of Action
   * @return List<Action> returns all actions for actionTypeName and state
   */
  List<Action> findByActionTypeNameAndStateOrderByCreatedDateTimeDesc(String actionTypeName,
                                                                      ActionDTO.ActionState state);

  /**
   * @param actionTypeName ActionTypeName filter criteria
   * @param limit          how many actions to return at most
   * @return Return all SUBMITTED or CANCEL_SUBMITTE Dactions for the specified actionTypeName
   */
  @Query(value = "SELECT "
      + " a.* "
      + "FROM action.action a "
      + " LEFT OUTER JOIN action.actionType at "
      + " ON a.actiontypefk = actiontypepk "
      + "WHERE "
      + " at.name = :actionTypeName "
      + " AND (a.statefk in ('SUBMITTED', 'CANCEL_SUBMITTED')) "
      + "ORDER BY updatedDateTime asc "
      + "LIMIT :limit "
      + "FOR UPDATE SKIP LOCKED", nativeQuery = true)
  List<Action> findSubmittedOrCancelledByActionTypeName(@Param("actionTypeName") String actionTypeName,
                                                        @Param("limit") int limit);

  /**
   * Return all actions for the specified actionTypeName.
   *
   * @param actionTypeName ActionTypeName filter criteria
   * @return List<Action> returns all actions for actionTypeName
   */
  List<Action> findByActionTypeNameOrderByCreatedDateTimeDesc(String actionTypeName);

  /**
   * Return all actions for the specified state.
   *
   * @param state State filter criteria
   * @return List<Action> returns all actions for state
   */
  List<Action> findByStateOrderByCreatedDateTimeDesc(ActionDTO.ActionState state);

}
