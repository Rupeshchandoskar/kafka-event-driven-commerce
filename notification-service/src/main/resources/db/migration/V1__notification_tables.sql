CREATE TABLE notifications
(
    id         VARCHAR(50) PRIMARY KEY,
    order_id   VARCHAR(50),
    message    TEXT,
    status     VARCHAR(20),
    created_at TIMESTAMP
);

CREATE TABLE failed_kafka_events
(
    id        VARCHAR(50) PRIMARY KEY,
    topic     VARCHAR(255),
    event_id  VARCHAR(255),
    order_id  VARCHAR(255),
    payload   TEXT,
    failed_at TIMESTAMP
);