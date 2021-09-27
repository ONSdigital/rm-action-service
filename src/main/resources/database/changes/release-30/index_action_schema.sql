-- ACTION SERVICE



-- action_event table


-- Index: action_event_case_id_index

CREATE INDEX action_event_case_id_index ON action.action_event USING btree (case_id);


-- Index: action_event_type_index

CREATE INDEX action_event_type_index ON action.action_event USING btree (type);


-- Index: action_event_handler_index

CREATE INDEX action_event_handler_index ON action.action_event USING btree (handler);


-- Index: action_eventevent_tag_index

CREATE INDEX action_event_event_tag_index ON action.action_event USING btree (event_tag);


-- Index: action_event_status_index

CREATE INDEX action_event_status_index ON action.action_event USING btree (status);

