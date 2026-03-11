package com.eventcommerce.payment.consumer;

import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.OrderCreatedPayload;
import com.eventcommerce.payment.service.FailedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDLTConsumer {

    private final FailedEventService failedEventService;

    @KafkaListener(
            topics = "order-created-dlt",
            groupId = "payment-dlt-group"
    )
    public void listen(BaseEvent<OrderCreatedPayload> event) {

        log.error(
                "DLQ event received | eventId={} orderId={}",
                event.getEventId(),
                event.getPayload().getOrderId()
        );

        failedEventService.saveFailedEvent(
                "order-created-dlt",
                event.getEventId(),
                event.getPayload().getOrderId(),
                event
        );

    }

}