package com.eventcommerce.payment.service;

import com.eventcommerce.payment.entity.FailedKafkaEvent;
import com.eventcommerce.payment.repository.FailedKafkaEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FailedEventService {

    private final FailedKafkaEventRepository repository;
    private final ObjectMapper objectMapper;

    public void saveFailedEvent(
            String topic,
            String eventId,
            String orderId,
            Object payload
    ) {

        try {

            FailedKafkaEvent failedEvent = new FailedKafkaEvent();

            failedEvent.setTopic(topic);
            failedEvent.setEventId(eventId);
            failedEvent.setOrderId(orderId);
            failedEvent.setPayload(objectMapper.writeValueAsString(payload));
            failedEvent.setFailedAt(Instant.now());

            repository.save(failedEvent);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
