CREATE TABLE orders
(
    id         VARCHAR(50) PRIMARY KEY,
    user_id    VARCHAR(50)    NOT NULL,
    amount     DECIMAL(12, 2) NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);