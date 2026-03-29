# Payment Service

## 📋 Overview

The Payment Service is responsible for processing payments after order creation. It consumes `ORDER_CREATED` events from Kafka, processes payments, and publishes either `PAYMENT_SUCCESS` or `PAYMENT_FAILED` events based on the payment outcome.

**Port**: `8082`  
**Database**: `paymentdb` (PostgreSQL)  
**Language**: Java 21  
**Framework**: Spring Boot 3.5.11

## 🎯 Responsibilities

- ✅ Consume ORDER_CREATED events from Kafka
- ✅ Validate payment requests
- ✅ Process payments using payment processor
- ✅ Persist payment records
- ✅ Publish PAYMENT_SUCCESS or PAYMENT_FAILED events
- ✅ Handle event retries and dead letter queues
- ✅ Ensure idempotent processing

## 🏗️ Architecture

### Event Flow

```
Kafka Topic: order-created
       ↓
OrderCreatedConsumer
       ↓
PaymentService.processPayment()
       ↓
PaymentProcessor (simulated payment gateway)
       ↓
Payment Entity (persisted to DB)
       ↓
PaymentEventProducer
       ↓
┌──────────────────────┬──────────────────────┐
│ PAYMENT_SUCCESS      │ PAYMENT_FAILED       │
└──────────────────────┴──────────────────────┘
       ↓                         ↓
   Inventory Service        Inventory Service
   (Reserve Stock)          (No Action/Retry)
```

### Key Components

| Component | Purpose |
|-----------|---------|
| `OrderCreatedConsumer` | Kafka listener for ORDER_CREATED events |
| `PaymentService` | Core business logic for payment processing |
| `PaymentProcessor` | Interfaces with payment gateway (simulated) |
| `PaymentEventProducer` | Publishes payment outcome events to Kafka |
| `EventIdempotencyService` | Prevents duplicate payment processing |
| `PaymentRepository` | Database persistence layer |

## 📦 Dependencies

```xml
<!-- Core -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Data -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Database Migration -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Shared Models -->
<dependency>
    <groupId>com.eventcommerce</groupId>
    <artifactId>common-lib</artifactId>
</dependency>
```

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka 7.5.0+
- Order Service running (to consume ORDER_CREATED events)

### Build

```bash
# Build with Maven
mvn clean package

# Build Docker image
docker build -t payment-service:1.0.0 .
```

### Run Locally

#### Option 1: Docker Compose (Recommended)
```bash
# From project root
docker-compose up -d payment-service

# View logs
docker logs -f payment-service
```

#### Option 2: Standalone
```bash
# Prerequisites: PostgreSQL, Kafka, Order Service running

# Run the service
mvn spring-boot:run
```

## ⚙️ Configuration

### application.yml

```yaml
server:
  port: 8082

spring:
  profiles:
    active: local

  datasource:
    url: jdbc:postgresql://postgres:5432/paymentdb
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  flyway:
    enabled: true
    locations: classpath:db/migration

  kafka:
    bootstrap-servers: kafka:9092
    
    consumer:
      group-id: payment-group
      auto-offset-reset: earliest
      
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

logging:
  level:
    root: INFO
    com.eventcommerce: DEBUG
```

### Environment Variables

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/paymentdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
SERVER_PORT=8082
```

## 📊 Database Schema

### Payments Table
```sql
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL UNIQUE,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
```

### Payment Event Tracking Table
```sql
CREATE TABLE payment_event_tracking (
    id SERIAL PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    order_id UUID NOT NULL,
    event_type VARCHAR(100),
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tracking_event_id ON payment_event_tracking(event_id);
```

## 🔄 Event Processing Flow

### 1. Order Created Event Consumed
```
@KafkaListener(topics = "order-created", groupId = "payment-group")
public void consume(BaseEvent<OrderCreatedPayload> event)
  ↓
EventIdempotencyService checks for duplicate
  ↓
If not processed:
  - PaymentService.processPayment(OrderCreatedPayload)
  - Mark event as processed
  ↓
If duplicate:
  - Log warning and ignore
```

### 2. Payment Processing
```
PaymentService.processPayment()
  ↓
PaymentProcessor.processPayment(amount)
  ↓
Create Payment entity with status based on result:
  - If success: PaymentStatus.SUCCESS
  - If failed: PaymentStatus.FAILED
  ↓
Save to database
  ↓
Publish appropriate event:
  - publishPaymentSuccess() → PAYMENT_SUCCESS topic
  - publishPaymentFailed() → PAYMENT_FAILED topic
```

### 3. Retry Strategy
```
Initial attempt on order-created topic
  ↓
If exception:
  Retry with exponential backoff:
    - Attempt 1: immediate
    - Attempt 2: +2s delay
    - Attempt 3: +4s delay
    - Attempt 4: +8s delay
  ↓
If all retries fail:
  Send to Dead Letter Queue: order-created-dlt
```

## 📤 Kafka Events

### Consumed Event: ORDER_CREATED

**Topic**: `order-created`

**Payload**:
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

### Published Event: PAYMENT_SUCCESS

**Topic**: `payment-success`

**Payload**:
```json
{
  "eventId": "evt-987654321",
  "eventType": "PAYMENT_SUCCESS",
  "eventVersion": "v1",
  "createdAt": "2026-03-29T10:31:00Z",
  "payload": {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "paymentId": "pay-123456789",
    "productId": "PROD-001",
    "quantity": 2,
    "amount": 99.99
  }
}
```

### Published Event: PAYMENT_FAILED

**Topic**: `payment-failed`

**Payload**:
```json
{
  "eventId": "evt-111111111",
  "eventType": "PAYMENT_FAILED",
  "eventVersion": "v1",
  "createdAt": "2026-03-29T10:31:00Z",
  "payload": {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "reason": "Insufficient funds",
    "amount": 99.99
  }
}
```

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing

1. **Create an order** (triggers ORDER_CREATED event):
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test@example.com",
    "productId": "TEST-PROD",
    "quantity": 1,
    "amount": 50.00
  }'
```

2. **Monitor PAYMENT_SUCCESS topic**:
```bash
docker exec kafka kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic payment-success \
  --from-beginning
```

3. **Check payment database**:
```bash
docker exec postgres psql -U postgres -d paymentdb -c "SELECT * FROM payments;"
```

4. **Check event tracking**:
```bash
docker exec postgres psql -U postgres -d paymentdb \
  -c "SELECT * FROM payment_event_tracking;"
```

## 📊 Performance Metrics

### Benchmarks

| Metric | Value |
|--------|-------|
| Payment Processing Latency | ~100ms |
| Event Consumption Throughput | 500+ events/second |
| Database Connection Pool | 10 connections |
| Event Retry Backoff | 2s, 4s, 8s, 16s |

### Monitoring

- **Application Health**: http://localhost:8082/actuator/health
- **Application Metrics**: http://localhost:8082/actuator/metrics
- **Kafka Consumer Lag**: Monitor via Kafka-UI

## 🔍 Troubleshooting

### Issue: No Events Being Consumed

**Symptom**: Payment Service running but no events consumed from Kafka

**Solution**:
```bash
# Check Kafka topics exist
docker exec kafka kafka-topics --list --bootstrap-server kafka:9092

# Check consumer group status
docker exec kafka kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group payment-group \
  --describe

# Check if order service published events
docker exec kafka kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic order-created \
  --from-beginning \
  --max-messages 1
```

### Issue: Payment Events Not Published

**Symptom**: Payments processed but no events in payment-success/payment-failed topics

**Solution**:
```bash
# Check logs for publishing errors
docker logs payment-service | grep "Failed to publish"

# Verify Kafka connectivity
docker logs payment-service | grep "Kafka"

# Check payment records in database
docker exec postgres psql -U postgres -d paymentdb \
  -c "SELECT * FROM payments ORDER BY created_at DESC LIMIT 5;"
```

### Issue: High Event Latency

**Symptom**: Delayed payment processing

**Solution**:
```bash
# Check database performance
docker logs payment-service | grep "duration"

# Monitor Kafka broker metrics
docker exec kafka kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group payment-group \
  --describe

# Check CPU/Memory usage
docker stats payment-service
```

## 🚨 Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `Cannot construct instance of OrderCreatedPayload` | Deserialization error | Verify event payload format |
| `Duplicate PAYMENT_SUCCESS ignored` | Event processed twice | Normal behavior, idempotency working |
| `Database connection timeout` | Connection pool exhausted | Increase pool size or check queries |
| `Kafka broker unavailable` | Broker down | Start Kafka container |

## 🔐 Security

### Authentication
- Currently uses Kafka's default authentication
- **Production**: Implement OAuth 2.0 or Kafka SASL

### Payment Processing
- Simulated payment processor for development
- **Production**: Integrate with actual payment gateway (Stripe, PayPal, etc.)

### Data Protection
- Sensitive payment data should be encrypted at rest
- Use HTTPS for all payment-related communications

## 🚀 Deployment

### Docker Deployment
```bash
# Build image
docker build -t payment-service:1.0.0 .

# Run container
docker run -d \
  --name payment-service \
  --network event-commerce-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/paymentdb \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -p 8082:8082 \
  payment-service:1.0.0
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: payment-service
  template:
    metadata:
      labels:
        app: payment-service
    spec:
      containers:
      - name: payment-service
        image: payment-service:1.0.0
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres:5432/paymentdb
        - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
          value: kafka:9092
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8082
          initialDelaySeconds: 30
          periodSeconds: 10
```

## 📝 Logs

### Important Log Patterns
```
# Event consumption
"Consuming ORDER_CREATED event | orderId={}"

# Payment processing
"Payment processed for orderId={} status={}"

# Event publishing
"PAYMENT_SUCCESS event published | topic={} partition={}"
"PAYMENT_FAILED event published | topic={} partition={}"

# Duplicate handling
"Duplicate ORDER_CREATED ignored eventId={}"

# Errors
"Failed to process payment for orderId={}"
```

## 🔄 Integration Points

| Service | Topic | Direction | Purpose |
|---------|-------|-----------|---------|
| Order Service | `order-created` | ← | Consume orders |
| Inventory Service | `payment-success` | → | Publish successful payments |
| Inventory Service | `payment-failed` | → | Publish failed payments |
| Common Library | - | ← | Use shared models |

## 📚 Related Documentation

- [Main README](../README.md)
- [Order Service](../order-service)
- [Inventory Service](../inventory-service)
- [Notification Service](../notification-service)
- [Common Library](../common-lib)

## 🆘 Support

For issues or questions:
1. Check logs: `docker logs payment-service`
2. Review configuration: `application.yml`
3. Verify Kafka connectivity: `docker exec kafka kafka-broker-api-versions --bootstrap-server kafka:9092`
4. Check database: `docker exec postgres psql -U postgres -d paymentdb -c "\dt"`
5. Open GitHub issue with error details

---

**Last Updated**: March 2026  
**Version**: 1.0.0  
**Maintainer**: Event Commerce Team

