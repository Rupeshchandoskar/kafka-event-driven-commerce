package com.eventcommerce.inventory.consumer;

import com.eventcommerce.common.constants.KafkaTopics;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.PaymentSuccessPayload;
import com.eventcommerce.inventory.service.EventIdempotencyService;
import com.eventcommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final InventoryService inventoryService;
    private final EventIdempotencyService idempotencyService;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_SUCCESS,
            groupId = "inventory-group"
    )
    public void consume(BaseEvent<PaymentSuccessPayload> event) {

        String eventId = event.getEventId();

        if (idempotencyService.isDuplicate(eventId)) {
            log.warn("Duplicate PAYMENT_SUCCESS ignored eventId={}", eventId);
            return;
        }

        inventoryService.reserveInventory(event.getPayload());

        idempotencyService.markProcessed(
                eventId,
                event.getEventType()
        );
    }
}