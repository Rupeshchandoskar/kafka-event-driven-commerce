CREATE TABLE outbox_events
(

    id           VARCHAR(100) PRIMARY KEY,

    event_type   VARCHAR(100),

    aggregate_id VARCHAR(100),

    payload      TEXT,

    created_at   TIMESTAMP,

    published    BOOLEAN DEFAULT FALSE

);