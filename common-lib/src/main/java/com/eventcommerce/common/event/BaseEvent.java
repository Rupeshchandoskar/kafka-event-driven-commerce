package com.eventcommerce.common.event;

import lombok.Data;

import java.time.Instant;

@Data
public class BaseEvent<T> {

    private String eventId;

    private String eventType;

    private String eventVersion;

    private Instant createdAt;

    private T payload;

}