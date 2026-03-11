CREATE TABLE inventory
(
    product_id         VARCHAR(50) PRIMARY KEY,
    available_quantity INT
);

CREATE TABLE inventory_reservations
(
    id          VARCHAR(50) PRIMARY KEY,
    order_id    VARCHAR(50),
    product_id  VARCHAR(50),
    quantity    INT,
    reserved_at TIMESTAMP
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