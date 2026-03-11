CREATE TABLE processed_events
(
    event_id     VARCHAR(100) PRIMARY KEY,
    event_type   VARCHAR(100),
    processed_at TIMESTAMP
);