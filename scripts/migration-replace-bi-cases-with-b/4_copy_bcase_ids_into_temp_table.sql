-- Add B case id's to temp table for matching B cases
UPDATE casesvc.temp_cases AS tc1
   SET bcaseid = bicasegroup2.id
  FROM (SELECT bicasegroup.bicaseid, c1.id
        FROM casesvc."case" AS c1
               INNER JOIN (SELECT tc.bicaseid, c2.casegroupfk
                           FROM casesvc."case" AS c2
                                  INNER JOIN casesvc.temp_cases AS tc ON c2.id = tc.bicaseid) AS bicasegroup
                 ON c1.casegroupfk = bicasegroup.casegroupfk
                      AND c1.sampleunittype = 'B') AS bicasegroup2
 WHERE tc1.bicaseid = bicasegroup2.bicaseid;

-- Set B cases to be ACTIONABLE
UPDATE casesvc."case"
   SET statefk = 'ACTIONABLE'
  FROM casesvc.temp_cases
 WHERE casesvc."case".id = casesvc.temp_cases.bcaseid;

-- Set BI cases to be INACTIONABLE
UPDATE casesvc."case"
   SET statefk = 'INACTIONABLE'
  FROM casesvc.temp_cases
 WHERE casesvc."case".id = casesvc.temp_cases.bicaseid;

-- Delete duplicate b case ids
WITH no_dupes AS
  (SELECT DISTINCT ON (bcaseid) * FROM casesvc.temp_cases)
DELETE
  FROM casesvc.temp_cases tc
 WHERE tc.bicaseid NOT IN (SELECT bicaseid FROM no_dupes);