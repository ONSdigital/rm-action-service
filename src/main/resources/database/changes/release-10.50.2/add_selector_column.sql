SET SCHEMA 'action';
ALTER TABLE action.actionplan
ADD COLUMN selectors jsonb;