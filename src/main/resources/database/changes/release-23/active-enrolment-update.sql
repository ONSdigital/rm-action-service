alter table action.case
drop column active_enrolment boolean;

alter table action.case
add column active_enrolment boolean DEFAULT FALSE;