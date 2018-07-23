ALTER TABLE action.actionrule
ADD triggerdatetime timestamptz;

UPDATE action.actionrule
   SET triggerdatetime = action.case.actionplanstartdate + CONCAT(action.actionrule.daysoffset, ' days')::INTERVAL
   FROM action.case WHERE action.actionrule.actionplanfk = action.case.actionplanfk;

ALTER TABLE action.actionrule
DROP daysoffset;