-- create postgres extension to allow generation of v4 UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE action.actionrule
ADD COLUMN id uuid;

UPDATE action.actionrule
set id = gen_random_uuid()
