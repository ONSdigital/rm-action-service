UPDATE action.actionplan
    SET selectors = selectors - 'exerciseRef' - 'surveyRef';

UPDATE action.actionplan
    SET selectors = (
           CASE
             WHEN cto.sampleunittype = 'B'
                     THEN jsonb_set(selectors, '{activeEnrolment}', '"false"', true)
             WHEN cto.sampleunittype = 'BI'
                     THEN jsonb_set(selectors, '{activeEnrolment}', '"true"', true)
               END
           )
  FROM action.temp_cto AS cto
 WHERE id = cto.actionplanid;

UPDATE action.actionplan
   SET selectors = jsonb_set(selectors, '{collectionExerciseId}', CAST('"'||CAST(cto.collectionexerciseid AS text)||'"' AS jsonb) , true)
  FROM action.temp_cto AS cto
 WHERE id = cto.actionplanid;