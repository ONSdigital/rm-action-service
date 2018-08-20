UPDATE action.actionplan
    SET selectors = selectors - 'exerciseRef' - 'surveyRef';

UPDATE action.actionplan
    SET selectors = (
           CASE
             WHEN cto.sampleunittype = 'B'
                     THEN jsonb_set(coalesce(selectors, '{}'), '{activeEnrolment}', '"false"', true)
             WHEN cto.sampleunittype = 'BI'
                     THEN jsonb_set(coalesce(selectors, '{}'), '{activeEnrolment}', '"true"', true)
               END
           )
  FROM action.temp_cto AS cto
 WHERE id = cto.actionplanid;

UPDATE action.actionplan AS ap
   SET selectors = jsonb_set(coalesce(ap.selectors, '{}'), '{collectionExerciseId}', CAST('"'||CAST(cto.collectionexerciseid AS text)||'"' AS jsonb) , true)
  FROM action.temp_cto AS cto
 WHERE id = cto.actionplanid;
