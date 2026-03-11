package com.eventcommerce.inventory.producer;

import com.eventcommerce.common.constants.KafkaTopics;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.InventoryReservedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryReserved(BaseEvent<InventoryReservedPayload> event) {

        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error(
                                "Failed to publish INVENTORY_RESERVED | orderId={}",
                                event.getPayload().getOrderId(),
                                ex
                        );
                    } else {
                        log.info(
                                "INVENTORY_RESERVED published | partition={} offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }

                });

    }

}