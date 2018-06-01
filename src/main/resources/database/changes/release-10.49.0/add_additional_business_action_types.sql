INSERT INTO action.actiontype(
actiontypepk, name, description, handler, cancancel, responserequired)
VALUES (4,'BSNL','Business Survey Notification Letter','Printer',true,false)
ON CONFLICT DO NOTHING;

INSERT INTO action.actiontype(
actiontypepk, name, description, handler, cancancel, responserequired)
VALUES (5,'BSNE','Business Survey Notification Email','Notify',true,false)
ON CONFLICT DO NOTHING;

INSERT INTO action.actiontype(
actiontypepk, name, description, handler, cancancel, responserequired)
VALUES (6,'BSRL','Business Survey Reminder Letter','Printer',true,false)
ON CONFLICT DO NOTHING;

INSERT INTO action.actiontype(
actiontypepk, name, description, handler, cancancel, responserequired)
VALUES (7,'BSRE','Business Survey Reminder Email','Notify',true,false)
ON CONFLICT DO NOTHING;
