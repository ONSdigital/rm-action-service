package uk.gov.ons.ctp.response.action.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Modifying
  @Query(
      value =
          "INSERT INTO action.action("
              + "id,"
              + "actionpk,"
              + "caseid,"
              + "casefk,"
              + "actionplanfk,"
              + "actionrulefk"
              + ",actiontypefk,"
              + "createdby,"
              + "manuallycreated,"
              + "situation,"
              + "statefk,"
              + "createddatetime,"
              + "updateddatetime"
              + ") "
              + "SELECT "
              + "uuid_in(md5(random()\\:\\:text || clock_timestamp()\\:\\:text)\\:\\:cstring),"
              + "nextval('action.actionpkseq'),"
              + "l.id,"
              + "l.casepk,"
              + "l.actionplanfk,"
              + "l.actionrulepk,"
              + "l.actiontypefk,"
              + "'SYSTEM',"
              + "FALSE,"
              + "NULL,"
              + "'SUBMITTED',"
              + "current_timestamp,"
              + "current_timestamp "
              + "FROM ("
              + "SELECT "
              + "c.casepk,"
              + "c.id,"
              + "r.actionplanfk,"
              + "r.actionrulepk,"
              + "r.actiontypefk "
              + "FROM "
              + "action.actionrule r,"
              + "action.case c "
              + "WHERE  "
              + "c.actionplanfk =:planid "
              + "AND "
              + "r.actionplanfk = c.actionplanfk "
              + "AND "
              + "DATE_PART('Day',now() - r.triggerdatetime\\:\\:timestamptz) < 1 "
              + "EXCEPT "
              + "SELECT "
              + "a.casefk,"
              + "c.id,"
              + "a.actionplanfk,"
              + "a.actionrulefk,"
              + "a.actiontypefk "
              + "FROM "
              + "action.action a,"
              + "action.actionrule r,"
              + "action.case c "
              + "WHERE "
              + "c.actionplanfk = :planid "
              + " AND "
              + "c.casepk = a.casefk "
              + "AND "
              + " c.actionplanfk = a.actionplanfk "
              + "AND "
              + "a.actionplanfk = r.actionplanfk "
              + "AND "
              + "a.actionrulefk = r.actionrulepk"
              + ") l",
      nativeQuery = true)
  int createActionsForPlan(@Param("planid") int actionPlanId);
}
