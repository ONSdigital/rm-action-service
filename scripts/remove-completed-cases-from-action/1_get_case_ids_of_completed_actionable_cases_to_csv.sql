\copy (SELECT id FROM casesvc.case WHERE statefk = 'ACTIONABLE' and casegroupfk in (SELECT casegrouppk FROM casesvc.casegroup WHERE status = 'COMPLETEDBYPHONE' or status = 'NOLONGERREQUIRED' or status = 'COMPLETE')) TO STDOUT WITH CSV