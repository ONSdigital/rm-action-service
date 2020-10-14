CREATE TABLE action.address (
	sampleunitref varchar(20) NULL,
	addresstype varchar(6) NULL,
	estabtype varchar(6) NULL,
	category varchar(20) NULL,
	organisation_name varchar(60) NULL,
	address_line1 varchar(60) NULL,
	address_line2 varchar(60) NULL,
	locality varchar(35) NULL,
	town_name varchar(30) NULL,
	postcode varchar(8) NULL,
	lad varchar(9) NULL,
	latitude float8 NULL,
	longitude float8 NULL,
	country varchar(20) NULL,
	addresspk uuid NOT NULL,
	CONSTRAINT addresspk_pkey PRIMARY KEY (addresspk)
);

CREATE TABLE action.contact (
	contactpk serial NOT NULL,
	forename varchar(35) NULL,
	surname varchar(35) NULL,
	phonenumber varchar(20) NULL,
	emailaddress varchar(200) NULL,
	title varchar(20) NULL,
	CONSTRAINT contactpk_pkey PRIMARY KEY (contactpk)
);

CREATE TABLE action.exportjob (
	id uuid NOT NULL,
	CONSTRAINT exportjob_pkey PRIMARY KEY (id)
);

CREATE TABLE action.exportfile (
	id uuid NOT NULL,
	filename varchar(60) NULL,
	exportjobid uuid NULL,
	datesuccessfullysent timestamp NULL,
	status varchar(20) NULL,
	rowcount int4 NULL,
	CONSTRAINT exportfile_pkey PRIMARY KEY (id)
);


ALTER TABLE action.exportfile ADD CONSTRAINT exportfile_exportjobid_fkey FOREIGN KEY (exportjobid) REFERENCES action.exportjob(id);

CREATE TABLE action.actionrequest (
	actionrequestpk int8 NOT NULL,
	actionid uuid NOT NULL,
	responserequired bool NULL DEFAULT false,
	actionplanname varchar(100) NULL,
	actiontypename varchar(100) NOT NULL,
	questionset varchar(10) NULL,
	contactfk int8 NULL,
	sampleunitref varchar(20) NOT NULL,
	caseid uuid NOT NULL,
	priority varchar(10) NULL,
	caseref varchar(16) NULL,
	iac varchar(24) NOT NULL,
	datestored timestamptz NULL,
	exerciseref varchar(20) NOT NULL,
	legalbasis varchar(50) NULL,
	region varchar(50) NULL,
	respondentstatus varchar(50) NULL,
	enrolmentstatus varchar(50) NULL,
	casegroupstatus varchar(50) NULL,
	surveyref varchar(50) NULL,
	returnbydate varchar(20) NULL,
	exportjobid uuid NULL,
	addressfk uuid NULL,
	CONSTRAINT actionrequestpk_pkey PRIMARY KEY (actionrequestpk)
);
CREATE INDEX actionrequest_addressfk_index ON action.actionrequest USING btree (addressfk);
CREATE INDEX actionrequest_contactfk_index ON action.actionrequest USING btree (contactfk);


-- action.actionrequest foreign keys

ALTER TABLE action.actionrequest ADD CONSTRAINT actionrequestexportjobid_fkey FOREIGN KEY (exportjobid) REFERENCES action.exportjob(id);
ALTER TABLE action.actionrequest ADD CONSTRAINT addressfk_fkey FOREIGN KEY (addressfk) REFERENCES action.address(addresspk);
ALTER TABLE action.actionrequest ADD CONSTRAINT contactfk_fkey FOREIGN KEY (contactfk) REFERENCES action.contact(contactpk) ON DELETE CASCADE;