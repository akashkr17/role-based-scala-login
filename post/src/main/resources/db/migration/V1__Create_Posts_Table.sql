-- -----------------------------------------------------
-- schema `post`
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS post;
CREATE TABLE post (
    post_id varchar,
	user_id varchar NOT NULL,
	title VARCHAR NOT NULL,
	text VARCHAR,
	created_at TIMESTAMP,
	primary key (post_id)
);
