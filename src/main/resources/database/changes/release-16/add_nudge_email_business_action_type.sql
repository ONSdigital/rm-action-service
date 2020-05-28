INSERT INTO action.actiontype(
actiontypepk, name, description, handler, cancancel, responserequired)
VALUES (13,'BSNUE','Business Survey Nudge Email','Notify',true,false)
ON CONFLICT DO NOTHING;

INSERT INTO action.actiontype(
actiontypepk, name, description, handler, cancancel, responserequired)
VALUES (14,'BSNUL','Business Survey Nudge Letter','Notify',true,false)
ON CONFLICT DO NOTHING;
