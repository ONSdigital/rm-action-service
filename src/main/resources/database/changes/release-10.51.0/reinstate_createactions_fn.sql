-- Function: action.createactions(integer)
-- DROP FUNCTION action.createactions(integer);

CREATE OR REPLACE FUNCTION action.createactions(p_actionplanjobPK integer)
  RETURNS boolean AS
$BODY$

DECLARE
  v_text             text;
  v_plan_name        text;
  v_plan_description text;
  v_errmess          text;
  v_actionplanid     integer;
  v_currentdatetime  timestamp;
  v_number_of_rows   integer;

BEGIN

  SELECT j.actionplanFK FROM action.actionplanjob j WHERE j.actionplanjobPK = p_actionplanjobPK INTO v_actionplanid;

  v_currentdatetime := current_timestamp;
  --v_currentdatetime := '2016-09-09 01:00:01+01'; -- for testing


  v_number_of_rows := 0;

  -- Look at the case table to see if any cases are due to run for the actionplan passed in
  -- start date before or equal current date
  -- end date after or equal current date
  -- rules found, for plan passed in, due as days offset is less than or equal to days passed since start date (current date minus start date)
  IF EXISTS (SELECT 1
             FROM action.case c, action.actionrule r
             WHERE c.actionplanstartdate <= v_currentdatetime AND c.actionplanenddate >= v_currentdatetime
                   AND r.daysoffset <= EXTRACT(DAY FROM (v_currentdatetime - c.actionplanstartdate))
                   AND c.actionplanFk = v_actionplanid
                   AND r.actionplanFK = c.actionplanFK) THEN

    -- Get plan description for messagelog using the actionplan passed in
    SELECT p.name, p.description
    FROM action.actionplan p
    WHERE p.actionplanPK = v_actionplanid INTO v_plan_name,v_plan_description;

    -- Collection Exercise start date reached, Run the rules due
    INSERT INTO action.action
    (
      id
      ,actionPK
      ,caseId
      ,caseFK
      ,actionplanFK
      ,actionruleFK
      ,actiontypeFK
      ,createdby
      ,manuallycreated
      ,situation
      ,stateFK
      ,createddatetime
      ,updateddatetime
    )
      SELECT
        gen_random_uuid()
        ,nextval('action.actionPKseq')
        ,l.id
        ,l.casePK
        ,l.actionplanFk
        ,l.actionrulePK
        ,l.actiontypeFK
        ,'SYSTEM'
        ,FALSE
        ,NULL
        ,'SUBMITTED'
        ,v_currentdatetime
        ,v_currentdatetime
      FROM
        (SELECT c.id
           ,c.casePK
           ,r.actionplanFK
           ,r.actionrulePK
           ,r.actiontypeFK
         FROM action.actionrule r
           ,action.case c
         WHERE  c.actionplanFk = v_actionplanid
                AND    r.actionplanFk = c.actionplanFK
                AND r.daysoffset <= EXTRACT(DAY FROM (v_currentdatetime - c.actionplanstartdate)) -- looking at start date to see if the rule is due
                AND c.actionplanstartdate <= v_currentdatetime AND c.actionplanenddate >= v_currentdatetime -- start date before or equal current date AND end date after or equal current date
         EXCEPT
         SELECT a.caseId
           ,a.caseFK
           ,a.actionplanFK
           ,a.actionruleFK
           ,a.actiontypeFK
         FROM action.action a
         WHERE a.actionplanFk = v_actionplanid) l;

    GET DIAGNOSTICS v_number_of_rows = ROW_COUNT; -- number of actions inserted

    IF v_number_of_rows > 0 THEN
      v_text := v_number_of_rows  || ' ACTIONS CREATED: ' || v_plan_description || ' (PLAN NAME: ' || v_plan_name || ') (PLAN ID: ' || v_actionplanid || ')';
      PERFORM action.logmessage(p_messagetext := v_text
      ,p_jobid := p_actionplanjobPK
      ,p_messagelevel := 'INFO'
      ,p_functionname := 'action.createactions');
    END IF;
  END IF;

  -- Update the date the actionplan was run on the actionplan table
  UPDATE action.actionplan
  SET lastrundatetime = v_currentdatetime
  WHERE actionplanPK  = v_actionplanid;

  -- Update the date the actionplan was run on the actionplanjob table
  UPDATE action.actionplanjob
  SET updateddatetime = v_currentdatetime
    ,stateFK = 'COMPLETED'
  WHERE actionplanjobPK =  p_actionplanjobPK
        AND   actionplanFK    =  v_actionplanid;

  RETURN TRUE;

  EXCEPTION

  WHEN OTHERS THEN
    v_errmess := SQLSTATE;
    PERFORM action.logmessage(p_messagetext := 'CREATE ACTION(S) EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || v_errmess
    ,p_jobid := p_actionplanjobPK
    ,p_messagelevel := 'FATAL'
    ,p_functionname := 'action.createactions');
    RETURN FALSE;
END;

$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

