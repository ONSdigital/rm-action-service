CREATE TABLE casesvc.temp_cases
(
  bicaseid uuid NOT NULL,
  bcaseid uuid,
  CONSTRAINT temp_cases_pkey PRIMARY KEY (bicaseid)
);