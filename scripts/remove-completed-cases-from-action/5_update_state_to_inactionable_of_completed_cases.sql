UPDATE casesvc.case
SET statefk = 'INACTIONABLE'
WHERE statefk = 'ACTIONABLE' and casegroupfk in (SELECT casegrouppk FROM casesvc.casegroup
WHERE status = 'COMPLETEDBYPHONE' or status = 'NOLONGERREQUIRED' or status = 'COMPLETE'));