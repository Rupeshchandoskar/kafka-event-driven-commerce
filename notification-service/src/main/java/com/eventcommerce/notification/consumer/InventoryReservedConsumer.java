package com.eventcommerce.notification.consumer;

import com.eventcommerce.common.constants.KafkaTopics;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.InventoryReservedPayload;
import com.eventcommerce.notification.service.EventIdempotencyService;
import com.eventcommerce.notification.service.NotificationService;
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
public class InventoryReservedConsumer {

    private final NotificationService notificationService;
    private final EventIdempotencyService idempotencyService;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = KafkaTopics.INVENTORY_RESERVED,
            groupId = "notification-group"
    )
    public void consume(BaseEvent<InventoryReservedPayload> event) {

        String eventId = event.getEventId();

        if (idempotencyService.isDuplicate(eventId)) {
            log.warn("Duplicate INVENTORY_RESERVED ignored eventId={}", eventId);
            return;
        }

        notificationService.sendNotification(event.getPayload());

        idempotencyService.markProcessed(
                eventId,
                event.getEventType()
        );
    }
}