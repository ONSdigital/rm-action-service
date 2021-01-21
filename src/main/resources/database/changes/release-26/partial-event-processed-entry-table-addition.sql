CREATE TABLE action.action_partial_event_process_entry (
    id BIGSERIAL PRIMARY KEY,
    collection_exercise_id uuid NOT NULL,
    event_tag varchar(100) NOT NULL,
    status varchar CHECK (status = 'COMPLETED' OR status = 'PARTIAL'),
    processed_cases bigint,
    pending_cases bigint,
    last_processed_timestamp TIMESTAMP);

ALTER TABLE action.action_event
ALTER COLUMN id TYPE bigint;