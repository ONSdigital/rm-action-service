package uk.gov.ons.ctp.response.action.domain.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;

/** JPA Data Repository. */
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
   * Return all actions for the specified actionTypeName and state in created date time order
   * descending.
   *
   * @param actionTypeName ActionTypeName filter criteria
   * @param state State of Action
   * @return List<Action> returns all actions for actionTypeName and state
   */
  List<Action> findByActionTypeNameAndStateOrderByCreatedDateTimeDesc(
      String actionTypeName, ActionDTO.ActionState state);

  Stream<Action> findByActionTypeAndStateIn(ActionType actionType, Set<ActionState> states);

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

  /**
   * Return boolean for if any actions exist for a given case id and action rule
   *
   * @param caseId UUID of associated case
   * @param actionRuleFK reference to action rule
   * @return Action returns an action is
   */
  boolean existsByCaseIdAndActionRuleFK(UUID caseId, Integer actionRuleFK);
}
