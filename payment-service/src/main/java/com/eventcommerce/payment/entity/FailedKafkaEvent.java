package com.eventcommerce.payment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "failed_kafka_events")
public class FailedKafkaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String topic;

    private String eventId;

    private String orderId;

    @Column(length = 5000)
    private String payload;

    private Instant failedAt;

}