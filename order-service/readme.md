# Order Service

Order Service is responsible for creating orders and publishing ORDER_CREATED events to Kafka.

## Responsibilities

- Create new orders
- Persist order data
- Publish Kafka events

## Event Flow

Client → order-service → Kafka (ORDER_CREATED)

## Database

Table: orders

Columns

id  
user_id  
amount  
status  
created_at

## Kafka Topics

order-created

## Run Locally

Start PostgreSQL and Kafka.
