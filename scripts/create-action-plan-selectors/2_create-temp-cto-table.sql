CREATE TABLE action.temp_cto
(
  collectionexerciseid uuid NOT NULL,
  sampleunittype varchar(2) NOT NULL,
  actionplanid uuid NOT null,
  CONSTRAINT temp_cto_pkey PRIMARY KEY (collectionexerciseid, sampleunittype)
);