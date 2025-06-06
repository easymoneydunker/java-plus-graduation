CREATE SCHEMA IF NOT EXISTS event;

DROP TABLE IF EXISTS event.location CASCADE;
DROP TABLE IF EXISTS event.category CASCADE;
DROP TABLE IF EXISTS event.event CASCADE;
DROP TABLE IF EXISTS event.compilation_event CASCADE;
DROP TABLE IF EXISTS event.compilation CASCADE;
DROP TABLE IF EXISTS event.views CASCADE;

CREATE TABLE IF NOT EXISTS event.location
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    lat
    FLOAT
    NOT
    NULL,
    lon
    FLOAT
    NOT
    NULL,
    CONSTRAINT
    pk_location
    PRIMARY
    KEY
(
    id
)
    );

CREATE INDEX IF NOT EXISTS ix_location_on_lat_n_lon ON event.location (lat, lon);

CREATE TABLE IF NOT EXISTS event.category
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
),
    CONSTRAINT pk_category PRIMARY KEY
(
    id
),
    CONSTRAINT category_name_unique UNIQUE
(
    name
)
    );

CREATE TABLE IF NOT EXISTS event.event
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    category_id
    BIGINT,
    user_id
    BIGINT,
    location_id
    BIGINT,
    title
    VARCHAR
(
    120
),
    annotation TEXT,
    description TEXT,
    confirmed_requests INTEGER NOT NULL,
    participant_limit INTEGER NOT NULL,
    request_moderation BOOLEAN NOT NULL,
    paid BOOLEAN NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    event_date TIMESTAMP
                         WITHOUT TIME ZONE,
    published_on TIMESTAMP
                         WITHOUT TIME ZONE,
    state VARCHAR
(
    255
),
    CONSTRAINT pk_event PRIMARY KEY
(
    id
),
    FOREIGN KEY
(
    category_id
) REFERENCES event.category
(
    id
),
    FOREIGN KEY
(
    location_id
) REFERENCES event.location
(
    id
),
    FOREIGN KEY
(
    user_id
) REFERENCES "user".users
(
    id
)
    );

CREATE INDEX IF NOT EXISTS ix_event_on_user_id ON event.event (user_id);
CREATE INDEX IF NOT EXISTS ix_event_on_state ON event.event (state);
CREATE INDEX IF NOT EXISTS ix_event_on_category_id ON event.event (category_id);

CREATE TABLE IF NOT EXISTS event.views
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    event_id
    BIGINT
    NOT
    NULL,
    ip
    VARCHAR
(
    15
) NOT NULL,
    CONSTRAINT uq_views UNIQUE
(
    event_id,
    ip
),
    FOREIGN KEY
(
    event_id
) REFERENCES event.event
(
    id
)
    );

CREATE TABLE IF NOT EXISTS event.compilation
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    pinned
    BOOLEAN
    NOT
    NULL,
    title
    VARCHAR
(
    50
) NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY
(
    id
)
    );

CREATE INDEX IF NOT EXISTS ix_compilation_on_pinned ON event.compilation (pinned);

CREATE TABLE IF NOT EXISTS event.compilation_event
(
    compilation_id
    BIGINT
    NOT
    NULL,
    event_id
    BIGINT
    NOT
    NULL,
    PRIMARY
    KEY
(
    compilation_id,
    event_id
),
    FOREIGN KEY
(
    compilation_id
) REFERENCES event.compilation
(
    id
) ON DELETE CASCADE,
    FOREIGN KEY
(
    event_id
) REFERENCES event.event
(
    id
)
  ON DELETE CASCADE
    );
