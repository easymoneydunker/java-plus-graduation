CREATE SCHEMA IF NOT EXISTS comment;

DROP TABLE IF EXISTS comment.comments CASCADE;

CREATE TABLE IF NOT EXISTS comment.comments
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    text
    TEXT
    NOT
    NULL,
    event_id
    BIGINT,
    author_id
    BIGINT,
    created_on
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    PRIMARY
    KEY
(
    id
),
    FOREIGN KEY
(
    event_id
) REFERENCES event.event
(
    id
) ON DELETE CASCADE,
    FOREIGN KEY
(
    author_id
) REFERENCES "user".users
(
    id
)
  ON DELETE CASCADE
    );
