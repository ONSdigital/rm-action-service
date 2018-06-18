SET SCHEMA 'action';
CREATE SEQUENCE actionplanselectorseq;

CREATE TABLE actionplanselector
(
  actionplanselectorpk integer DEFAULT nextval('actionplanselectorseq') NOT NULL,
  actionplanfk integer NOT NULL,
  selectors jsonb NOT NULL,
  CONSTRAINT actionplanselectorpk_pkey PRIMARY KEY (actionplanselectorpk),
  CONSTRAINT actionplanfk_fkey FOREIGN KEY (actionplanfk)
      REFERENCES actionplan (actionplanpk) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);