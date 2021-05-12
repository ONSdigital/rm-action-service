ALTER TABLE action.case
RENAME COLUMN actionplanid TO action_plan_id
RENAME COLUMN actionplanstartdate TO action_plan_start_date
RENAME COLUMN actionplanfk to action_plan_fk
RENAME COLUMN actionplanenddate TO action_plan_end_date
RENAME COLUMN partyid TO party_id
RENAME COLUMN collectionexerciseid TO collection_exercise_id
RENAME COLUMN sampleunittype TO sample_unit_type
RENAME COLUMN casepk TO case_pk;

ALTER TABLE action.actionplan
RENAME COLUMN actionplanpk TO action_plan_pk
RENAME COLUMN createdby TO created_by
RENAME COLUMN lastrundatetime TO last_run_date_time;