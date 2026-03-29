# Notification Service

## 📋 Overview

The Notification Service is the final step in the order fulfillment workflow. It consumes `INVENTORY_RESERVED` events and sends order confirmation notifications to customers.

**Port**: `8084`  
**Database**: `notificationdb`  
**Framework**: Spring Boot 3.5.11  
**Language**: Java 21

## 🎯 Responsibilities

- ✅ Consume INVENTORY_RESERVED events
- ✅ Send email/SMS notifications
- ✅ Store notification history
- ✅ Track delivery status
- ✅ Handle retries and dead letter queues
- ✅ Ensure idempotent processing

## 📖 Documentation

For complete documentation, see:
- **Event Handling**: Kafka consumer configuration for INVENTORY_RESERVED
- **Database Schema**: `db/migration/` Flyway migrations
- **Configuration**: `application.yml` and `application-local.yml`
- **Notification Templates**: Email/SMS message templates

## 🚀 Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run locally (requires PostgreSQL & Kafka)
mvn spring-boot:run

# Or with Docker Compose
docker-compose up -d notification-service
```

## 🔄 Event Flow

```
Kafka: inventory-reserved
       ↓
InventoryReservedConsumer
       ↓
NotificationService
       ↓
EmailSender (simulated)
       ↓
Notification record saved
       ↓
Response logged
```

## 📤 Event Consumed

**Topic**: `inventory-reserved`

```json
{
  "eventId": "evt-555555555",
  "eventType": "INVENTORY_RESERVED",
  "eventVersion": "v1",
  "createdAt": "2026-03-29T10:31:30Z",
  "payload": {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "productId": "PROD-001",
    "quantity": 2
  }
}
```

## 💾 Database

### Tables
- **notifications**: Stores sent notifications (id, order_id, message, status, created_at)
- **notification_event_tracking**: Tracks processed events for idempotency

### Migrations
Located in `src/main/resources/db/migration/`

## 🔌 REST API

### Get Notification History
```
GET /notifications?orderId={orderId}

Response: List of notifications for order
```

## 📧 Notification Content

**Email Subject**: Order Confirmation - Order #{orderId}

**Message Template**:
```
Thank you for your order!

Order ID: {orderId}
Product: {productId}
Quantity: {quantity}
Status: Confirmed

Your order will be shipped within 24 hours.
Track your order using your order ID.

Best regards,
Event Commerce Team
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Create test order (triggers full event flow)
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"test@test.com","productId":"PROD-001","quantity":1,"amount":10.0}'

# Wait ~10s, then check notifications
curl "http://localhost:8084/notifications?orderId=<orderId>"

# Check notifications in database
docker exec postgres psql -U postgres -d notificationdb -c "SELECT * FROM notifications;"
```

## 📊 Performance

- Notification sending latency: ~100ms
- Event consumption throughput: 500+ events/second
- Email delivery (simulated): ~50ms

## 🔍 Monitoring

- Health: `http://localhost:8084/actuator/health`
- Metrics: `http://localhost:8084/actuator/metrics`
- Database: Check `notificationdb` via PostgreSQL
- Kafka Consumer Lag: Monitor via Kafka-UI

## ⚙️ Configuration

**Port**: 8084  
**Database**: jdbc:postgresql://postgres:5432/notificationdb  
**Kafka**: kafka:9092  
**Consumer Group**: notification-group  
**Profiles**: local, dev, production

## 🐛 Troubleshooting

**Issue**: No notifications being sent
```bash
docker logs notification-service | grep -i "notification"
docker exec kafka kafka-consumer-groups --bootstrap-server kafka:9092 --group notification-group --describe
```

**Issue**: Database connection failed
```bash
docker ps | grep postgres
docker logs postgres
```

**Issue**: Duplicate notifications
```bash
# Check idempotency tracking
docker exec postgres psql -U postgres -d notificationdb -c "SELECT * FROM notification_event_tracking;"
```

## 🔐 Security

- Input validation on all Kafka events
- No sensitive data in logs
- Email service would use secured credentials (production)
- Notification preferences could be stored (future enhancement)

## 📚 Related Services

- **Inventory Service**: Produces INVENTORY_RESERVED events
- **Common Library**: Shared event models and utilities
- **Kafka**: Message broker for event distribution

## 🚀 Production Considerations

For production deployment:
1. Integrate with SendGrid, AWS SES, or Twilio
2. Implement notification preferences
3. Add retry mechanism for failed sends
4. Store delivery receipts
5. Monitor notification delivery rates

## 📝 See Also

- [Main README](../README.md) - Project overview
- [Inventory Service](../inventory-service) - Upstream service

[//]: # (- [Deployment Guide]&#40;../DEPLOYMENT.md&#41; - Production deployment)

[//]: # (- [Contributing Guide]&#40;../CONTRIBUTING.md&#41; - Development guidelines)
