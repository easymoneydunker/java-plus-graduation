CREATE SCHEMA IF NOT EXISTS "user";

DROP TABLE IF EXISTS "user".users CASCADE;

CREATE TABLE IF NOT EXISTS "user".users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    254
) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY
(
    id
),
    CONSTRAINT users_email_unique UNIQUE
(
    email
)
    );
