package uk.gov.ons.ctp.response.action.domain.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;

/** JPA Data Repository. */
@Repository
public interface ActionRepository extends JpaRepository<Action, BigInteger> {

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

  Stream<Action> findByActionTypeAndStateIn(ActionType actionType, Set<ActionState> states);

  /**
   * Return boolean for if any actions exist for a given case id and action rule
   *
   * @param caseId UUID of associated case
   * @param actionRuleFK reference to action rule
   * @return Action returns an action is
   */
  boolean existsByCaseIdAndActionRuleFK(UUID caseId, Integer actionRuleFK);

  List<Action> findFirst10000ByActionTypeAndActionRuleFKAndStateIn(
      ActionType actionType, Integer actionRuleFk, Set<ActionState> states);

  /**
   * Return all the action rules ids which have the required action type and are in the required
   * state
   *
   * <p>This is usually "Printer" or "Notify" and "Submitted" state
   *
   * @param actionType the action type
   * @param states the states
   * @return the distinct
   */
  @Query("SELECT DISTINCT a.actionRuleFK FROM Action a WHERE a.actionType =?1 AND a.state IN ?2")
  List<Integer> findDistinctActionRuleFKByActionTypeAndStateIn(
      ActionType actionType, Set<ActionDTO.ActionState> states);
}
