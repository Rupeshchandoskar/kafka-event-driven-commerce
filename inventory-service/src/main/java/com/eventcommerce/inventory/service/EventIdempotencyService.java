package com.eventcommerce.inventory.service;


import com.eventcommerce.inventory.entity.ProcessedEvent;
import com.eventcommerce.inventory.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventIdempotencyService {

    private final ProcessedEventRepository repository;

    public boolean isDuplicate(String eventId) {

        return repository.existsById(eventId);

    }

    public void markProcessed(String eventId, String eventType) {

        ProcessedEvent event = new ProcessedEvent();

        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setProcessedAt(Instant.now());

        repository.save(event);
    }

}