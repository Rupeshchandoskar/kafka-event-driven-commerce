CREATE TABLE payments
(
    id         VARCHAR(50) PRIMARY KEY,
    order_id   VARCHAR(50)    NOT NULL,
    amount     DECIMAL(12, 2) NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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