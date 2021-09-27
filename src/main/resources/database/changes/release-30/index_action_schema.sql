-- ACTION SERVICE



-- action_event table


-- Index: action_eventcase_id_index
-- DROP INDEX action_eventcase_id_index;

CREATE INDEX action_event_case_id_index ON action.action_event USING btree (case_id);


-- Index: action_eventtype_index
-- DROP INDEX action_eventtype_index;

CREATE INDEX action_eventtype_index ON action.action_event USING btree (type);


-- Index: action_eventhandler_index
-- DROP INDEX action_eventhandler_index;

CREATE INDEX action_eventhandler_index ON action.action_event USING btree (handler);


-- Index: action_eventevent_tag_index
-- DROP INDEX action_eventevent_tag_index;

CREATE INDEX action_eventevent_tag_index ON action.action_event USING btree (event_tag);


-- Index: action_eventstatus_index
-- DROP INDEX action_eventstatus_index;

CREATE INDEX action_eventstatus_index ON action.action_event USING btree (status);

