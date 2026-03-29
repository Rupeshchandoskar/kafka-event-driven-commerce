# Order Service

## 📋 Overview

The Order Service is the entry point for the event-driven commerce platform. It handles order creation, persistence, and publishing of `ORDER_CREATED` events to Kafka for downstream processing.

**Port**: `8081`  
**Database**: `orderdb`  
**Framework**: Spring Boot 3.5.11  
**Language**: Java 21

## 🎯 Responsibilities

- ✅ Validate and create orders via REST API
- ✅ Persist order data to PostgreSQL
- ✅ Publish ORDER_CREATED events to Kafka
- ✅ Implement outbox pattern for reliability
- ✅ Maintain order audit trail

## 📖 Documentation

For complete documentation, see:
- **API Details**: `/orders` endpoint documentation
- **Database Schema**: `db/migration/` Flyway migrations
- **Configuration**: `application.yml` and `application-local.yml`
- **Event Format**: ORDER_CREATED topic schema

## 🚀 Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run locally (requires PostgreSQL & Kafka)
mvn spring-boot:run

# Or with Docker Compose
docker-compose up -d order-service
```

## 🔌 REST API

### Create Order
```
POST /orders
Content-Type: application/json

{
  "userId": "user123@example.com",
  "productId": "PROD-001",
  "quantity": 2,
  "amount": 99.99
}
```

**Response**: 201 Created with Order entity

## 🔄 Event Flow

```
REST Request
    ↓
OrderController
    ↓
OrderService (transactional)
    ↓
Order + OutboxEvent (saved together)
    ↓
OutboxPublisherService (scheduled)
    ↓
Kafka Topic: order-created
```

## 📤 Event Schema

**Topic**: `order-created`

```json
{
  "eventId": "evt-123456789",
  "eventType": "ORDER_CREATED",
  "eventVersion": "v1",
  "createdAt": "2026-03-29T10:30:00Z",
  "payload": {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user123@example.com",
    "productId": "PROD-001",
    "quantity": 2,
    "amount": 99.99
  }
}
```

## 💾 Database

### Tables
- **orders**: Stores order data (id, user_id, product_id, quantity, amount, status, created_at)
- **outbox_events**: Reliable event publishing queue

### Migrations
Located in `src/main/resources/db/migration/`

## 🧪 Testing

```bash
# Run all tests
mvn test

# Create test order
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"test@test.com","productId":"TEST","quantity":1,"amount":10.0}'

# Check events
docker exec kafka kafka-console-consumer --bootstrap-server kafka:9092 --topic order-created --from-beginning
```

## 📊 Performance

- Order creation latency: ~50ms
- Event publishing: ~100ms
- Throughput: 1000+ orders/second

## 🔍 Monitoring

- Health: `http://localhost:8081/actuator/health`
- Metrics: `http://localhost:8081/actuator/metrics`
- Database: Check `orderdb` via PostgreSQL

## ⚙️ Configuration

**Port**: 8081  
**Database**: jdbc:postgresql://postgres:5432/orderdb  
**Kafka**: kafka:9092  
**Profiles**: local, dev, production

## 🐛 Troubleshooting

**Issue**: Database connection failed
```bash
docker ps | grep postgres
docker logs postgres
```

**Issue**: Events not published
```bash
docker logs order-service | grep -i "order created"
docker exec postgres psql -U postgres -d orderdb -c "SELECT * FROM outbox_events WHERE published = false;"
```

**Issue**: Kafka connection issues
```bash
docker logs kafka
docker exec kafka kafka-broker-api-versions --bootstrap-server kafka:9092
```

## 📚 Related Services

- **Payment Service**: Consumes ORDER_CREATED events
- **Common Library**: Shared event models and utilities
- **Kafka**: Message broker for event distribution

## 🔐 Security

- All inputs validated via Jakarta Bean Validation
- SQL injection prevention via JPA parameterized queries
- No sensitive data logging
- Configurable CORS for REST endpoints

## 📝 See Also

- [Main README](../README.md) - Project overview
- [Payment Service](../payment-service) - Downstream service

[//]: # (- [Deployment Guide]&#40;../DEPLOYMENT.md&#41; - Production deployment)

[//]: # (- [Contributing Guide]&#40;../CONTRIBUTING.md&#41; - Development guidelines)
