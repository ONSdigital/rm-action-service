UPDATE action.case c
SET    active_enrolment = bool(A.selectors->>'activeEnrolment')
FROM   action.actionplan A
WHERE  A.id = c.actionplanid;