DO $$
DECLARE
  start_ap_pk integer;
  start_ar_pk integer;
  start_at_pk integer;

BEGIN
  -- get current max pk count + 1 for each table
  SELECT max(actionplanpk)+1 INTO start_ap_pk FROM action.actionplan;
  SELECT max(actionrulepk)+1 INTO start_ar_pk FROM action.actionrule;
  SELECT max(actiontypepk)+1 INTO start_at_pk FROM action.actiontype;


  EXECUTE 'CREATE SEQUENCE IF NOT EXISTS action.actionplanseq
  			  START WITH ' || start_ap_pk ||
          'INCREMENT BY 1
          OWNED BY action.actionplan.actionplanpk;

  			  CREATE SEQUENCE IF NOT EXISTS action.actionruleseq
      		START WITH ' || start_ar_pk ||
      		'INCREMENT BY 1
      		OWNED BY action.actionrule.actionrulepk;

  			  CREATE SEQUENCE IF NOT EXISTS action.actiontypeseq
      		START WITH ' || start_at_pk ||
      		'INCREMENT BY 1
      		OWNED BY action.actiontype.actiontypepk;';

END $$;
