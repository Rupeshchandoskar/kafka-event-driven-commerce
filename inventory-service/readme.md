# Inventory Service

Inventory Service manages stock reservations after payment completion.

## Event Flow

payment-service → Kafka → payment-success → inventory-service → inventory-reserved

## Responsibilities

- Consume payment-success events
- Reserve inventory
- Persist reservation data
- Handle retries and DLQ
- Maintain inventory consistency

## Database Tables

inventory  
inventory_reservations  
failed_kafka_events

## Tech Stack

Java 21  
Spring Boot 3.5.11  
Kafka  
PostgreSQL  
Flyway

## Run

mvn clean install  
mvn spring-boot:run