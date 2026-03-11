# Notification Service

Notification Service sends order confirmation messages after inventory reservation.

Event Flow

inventory-service → Kafka → inventory-reserved → notification-service

Responsibilities

• Consume inventory-reserved events
• Send email/SMS notification
• Store notification history
• Handle Kafka retries and DLQ

Database Tables

notifications
failed_kafka_events

Run

mvn clean install
mvn spring-boot:run

Tech Stack

Java 21
Spring Boot 3.5.11
Kafka
PostgreSQL
Flyway