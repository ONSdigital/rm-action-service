-- create postgres extension to allow generation of v4 UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE action.actionrule
ADD COLUMN id uuid;

UPDATE action.actionrule
SET id = public.gen_random_uuid();

ALTER TABLE action.actionrule
ALTER COLUMN id SET NOT NULL,
ADD CONSTRAINT u_id UNIQUE(id);
