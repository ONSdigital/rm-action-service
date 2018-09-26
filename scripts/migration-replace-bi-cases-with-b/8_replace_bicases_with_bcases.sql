-- Replace BI cases with B cases
UPDATE action."case" AS c
   SET id = tc.bcaseid,
       sampleunittype = 'B'
  FROM action.temp_cases AS tc
 WHERE c.id = tc.bicaseid;

-- Delete remaining BI cases
DELETE
  FROM action."case"
 WHERE sampleunittype = 'BI';