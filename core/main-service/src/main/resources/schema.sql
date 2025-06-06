DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS request CASCADE;
DROP TABLE IF EXISTS compilation_event CASCADE;
DROP TABLE IF EXISTS compilation CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS location
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

CREATE INDEX IF NOT EXISTS ix_location_on_lat_n_lon ON location (lat, lon);

CREATE TABLE IF NOT EXISTS category
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

CREATE TABLE IF NOT EXISTS users
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

CREATE TABLE IF NOT EXISTS event
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
    CONSTRAINT fk_event_on_category FOREIGN KEY
(
    category_id
) REFERENCES category
(
    id
),
    CONSTRAINT fk_event_on_location FOREIGN KEY
(
    location_id
) REFERENCES location
(
    id
),
    CONSTRAINT fk_event_on_user FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
)
    );

CREATE INDEX IF NOT EXISTS ix_event_on_user_id ON event (user_id);
CREATE INDEX IF NOT EXISTS ix_event_on_state ON event (state);
CREATE INDEX IF NOT EXISTS ix_event_on_category_id ON event (category_id);

CREATE TABLE IF NOT EXISTS views
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
) REFERENCES event
(
    id
)
    );

CREATE TABLE IF NOT EXISTS request
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
    NULL
    CONSTRAINT
    request_event_id_fk
    REFERENCES
    event
(
    id
),
    requester_id BIGINT NOT NULL CONSTRAINT request_users_id_fk REFERENCES users
(
    id
),
    status VARCHAR
(
    15
) NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY
(
    id
)
    );

CREATE INDEX IF NOT EXISTS ix_request_on_requester_id ON request (requester_id);
CREATE INDEX IF NOT EXISTS ix_request_on_event_id ON request (event_id);
CREATE INDEX IF NOT EXISTS ix_request_on_status ON request (status);

CREATE TABLE IF NOT EXISTS compilation
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

CREATE INDEX IF NOT EXISTS ix_compilation_on_pinned ON compilation (pinned);

CREATE TABLE IF NOT EXISTS compilation_event
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
) REFERENCES compilation
(
    id
) ON DELETE CASCADE,
    FOREIGN KEY
(
    event_id
) REFERENCES event
(
    id
)
  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comments
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
    BIGINT
    REFERENCES
    event
(
    id
) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    created_on TIMESTAMP
  WITHOUT TIME ZONE NOT NULL,
    PRIMARY KEY
(
    id
)
    );
