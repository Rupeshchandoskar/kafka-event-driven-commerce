package com.eventcommerce.notification.consumer;

import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.InventoryReservedPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDLTConsumer {

    @KafkaListener(
            topics = "inventory-reserved-dlt",
            groupId = "notification-dlt-group"
    )
    public void consume(BaseEvent<InventoryReservedPayload> event) {

        log.error(
                "DLQ event received | eventId={} orderId={}",
                event.getEventId(),
                event.getPayload().getOrderId()
        );

    }

}