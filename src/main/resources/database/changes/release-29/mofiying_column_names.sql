ALTER TABLE action.case RENAME COLUMN actionplanid TO action_plan_id;
ALTER TABLE action.case RENAME COLUMN actionplanstartdate TO action_plan_start_date;
ALTER TABLE action.case RENAME COLUMN actionplanfk to action_plan_fk;
ALTER TABLE action.case RENAME COLUMN actionplanenddate TO action_plan_end_date;
ALTER TABLE action.case RENAME COLUMN partyid TO party_id;
ALTER TABLE action.case RENAME COLUMN collectionexerciseid TO collection_exercise_id;
ALTER TABLE action.case RENAME COLUMN sampleunittype TO sample_unit_type;
ALTER TABLE action.case RENAME COLUMN casepk TO case_pk;

ALTER TABLE action.actionplan RENAME COLUMN actionplanpk TO action_plan_pk;
ALTER TABLE action.actionplan RENAME COLUMN createdby TO created_by;
ALTER TABLE action.actionplan RENAME COLUMN lastrundatetime TO last_run_date_time;