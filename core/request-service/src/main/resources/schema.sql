CREATE SCHEMA IF NOT EXISTS request;

DROP TABLE IF EXISTS request.request CASCADE;

CREATE TABLE IF NOT EXISTS request.request
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    created_on
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    event_id
    BIGINT
    NOT
    NULL,
    requester_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
(
    15
) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY
(
    id
)
    );
