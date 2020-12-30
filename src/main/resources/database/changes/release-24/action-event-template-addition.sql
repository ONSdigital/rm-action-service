alter table action.case
alter column actionplanid DROP NOT NULL;

alter table action.case
alter column actionplanfk DROP NOT NULL;

alter table action.case
alter column actionplanstartdate DROP NOT NULL;

alter table action.case
alter column actionplanenddate DROP NOT NULL;

CREATE TABLE action.action_template (
    type varchar (40) UNIQUE NOT NULL,
    description varchar(350) NOT NULL,
    event_tag_mapping varchar(100) NOT NULL,
    handler varchar CHECK (handler = 'EMAIL' OR handler = 'LETTER'),
    prefix varchar (100));

INSERT INTO action.action_template
    (type, description, event_tag_mapping, handler, prefix)
    VALUES
    ('BSNL', 'Business Survey Notification Letter', 'mps', 'LETTER', 'BSNOT'),
    ('BSNE', 'Business Survey Notification Email', 'go_live', 'EMAIL', NULL),
    ('BSRL', 'Business Survey Reminder Letter', 'reminder', 'LETTER', 'BSREM'),
    ('BSRE', 'Business Survey Reminder Email', 'reminder', 'EMAIL', NULL),
    ('BSNUE', 'Business Survey Nudge Email', 'nudge', 'EMAIL', NULL);

CREATE TABLE action.action_event (
    id SERIAL PRIMARY KEY,
    case_id uuid NOT NULL,
    type varchar (40) NOT NULL,
    collection_exercise_id uuid NOT NULL,
    survey_id uuid NOT NULL,
    handler varchar CHECK (handler = 'EMAIL' OR handler = 'LETTER'),
    status varchar CHECK (status = 'PROCESSED' OR status = 'FAILED'),
    processed_timestamp TIMESTAMP);
