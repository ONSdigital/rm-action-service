-- create postgres extension to allow generation of v4 UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto" SCHEMA "public";

CREATE USER actionsvc PASSWORD 'actionsvc'
  NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION INHERIT LOGIN;

CREATE SCHEMA action;

SET search_path TO action, public;
