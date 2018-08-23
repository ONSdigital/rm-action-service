package uk.gov.ons.ctp.response.action.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;

/** JPA Data Repository. */
@Repository
public interface ActionTypeRepository extends JpaRepository<ActionType, Integer> {

  /**
   * Return an actiontype by name
   *
   * @param name ActionTypeName filter criteria
   * @return ActionType the action type found or null if not
   */
  ActionType findByName(String name);

  /**
   * Return an actiontype primary key
   *
   * @param actionTypePK Reference to action type
   * @return ActionType the action type found or null if not
   */
  ActionType findByActionTypePK(Integer actionTypePK);
}
