package com.eventcommerce.order.service;

import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.OrderCreatedPayload;
import com.eventcommerce.order.entity.OutboxEvent;
import com.eventcommerce.order.producer.OrderEventProducer;
import com.eventcommerce.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private final OutboxEventRepository repository;
    private final OrderEventProducer producer;
    private final ObjectMapper objectMapper;

    public void publishEvents() {

        List<OutboxEvent> events =
                repository.findTop50ByPublishedFalseOrderByCreatedAt();

        for (OutboxEvent event : events) {

            try {

                BaseEvent<OrderCreatedPayload> kafkaEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                objectMapper.getTypeFactory()
                                        .constructParametricType(
                                                BaseEvent.class,
                                                OrderCreatedPayload.class
                                        )
                        );

                producer.publishOrderCreatedEvent(kafkaEvent);

                event.setPublished(true);
                repository.save(event);

                log.info("Outbox event published eventId={}", event.getId());

            } catch (Exception e) {

                log.error("Failed publishing outbox event {}", event.getId(), e);

            }

        }

    }

}