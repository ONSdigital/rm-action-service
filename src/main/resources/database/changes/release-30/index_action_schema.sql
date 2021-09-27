CREATE INDEX action_event_case_id_index ON action.action_event USING btree (case_id);
CREATE INDEX action_event_type_index ON action.action_event USING btree (type);
CREATE INDEX action_event_handler_index ON action.action_event USING btree (handler);
CREATE INDEX action_event_event_tag_index ON action.action_event USING btree (event_tag);
CREATE INDEX action_event_status_index ON action.action_event USING btree (status);
