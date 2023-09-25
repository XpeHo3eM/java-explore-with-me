CREATE TABLE IF NOT EXISTS users (
    id    BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL UNIQUE,
    name  VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id   BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
    id                 BIGINT    GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title              VARCHAR   NOT NULL,
    annotation         VARCHAR   NOT NULL,
    category_id        BIGINT    NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    description        VARCHAR   NOT NULL,
    event_date         TIMESTAMP NOT NULL,
    location_lat       NUMERIC   NOT NULL,
    location_lon       NUMERIC   NOT NULL,
    paid               BOOLEAN   NOT NULL,
    participant_limit  BIGINT    NOT NULL,
    confirmed_requests BIGINT    NOT NULL,
    request_moderation BOOLEAN   NOT NULL,
    initiator_id       BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    state              VARCHAR   NOT NULL,
    created_on         TIMESTAMP NOT NULL,
    published_on       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS compilations (
    id     BIGINT  GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN DEFAULT FALSE,
    title  VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    id           BIGINT    NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id     BIGINT    NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    requester_id BIGINT    NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    status       VARCHAR   NOT NULL,
    created_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id       BIGINT NOT NULL REFERENCES events(id)       ON DELETE CASCADE,
    PRIMARY KEY(compilation_id, event_id)
);