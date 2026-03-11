package com.eventcommerce.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private String id;

    private String eventType;

    private String aggregateId;

    private String payload;

    private Instant createdAt;

    private Boolean published;

}