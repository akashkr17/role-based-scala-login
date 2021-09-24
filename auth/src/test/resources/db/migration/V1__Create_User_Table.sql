-- -----------------------------------------------------
-- schema `auth`
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS auth;
drop table if exists auth.user_login;
CREATE TABLE IF NOT EXISTS auth.user_login (
	user_id varchar,
	email VARCHAR NOT NULL ,
	password VARCHAR,
	created_at TIMESTAMP,
	last_login TIMESTAMP,
	primary key (user_id,email)
);
