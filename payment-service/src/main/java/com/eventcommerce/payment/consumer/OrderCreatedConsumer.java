package com.eventcommerce.payment.consumer;

import com.eventcommerce.common.constants.KafkaTopics;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.OrderCreatedPayload;
import com.eventcommerce.payment.service.EventIdempotencyService;
import com.eventcommerce.payment.service.PaymentService;
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
public class OrderCreatedConsumer {

    private final PaymentService paymentService;
    private final EventIdempotencyService idempotencyService;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "payment-group"
    )
    public void consume(BaseEvent<OrderCreatedPayload> event) {

        String eventId = event.getEventId();

        if (idempotencyService.isDuplicate(eventId)) {
            log.warn("Duplicate ORDER_CREATED ignored eventId={}", eventId);
            return;
        }

        paymentService.processPayment(event.getPayload());

        idempotencyService.markProcessed(
                eventId,
                event.getEventType()
        );
    }
}