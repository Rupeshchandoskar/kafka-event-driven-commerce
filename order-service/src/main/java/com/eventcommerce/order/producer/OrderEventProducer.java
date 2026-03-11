package com.eventcommerce.order.producer;

import com.eventcommerce.common.constants.KafkaTopics;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.OrderCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreatedEvent(BaseEvent<OrderCreatedPayload> event) {

        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error(
                                "Failed to publish ORDER_CREATED event | eventId={} orderId={}",
                                event.getEventId(),
                                event.getPayload().getOrderId(),
                                ex
                        );
                    } else {
                        log.info(
                                "ORDER_CREATED event published | topic={} partition={} offset={} orderId={}",
                                KafkaTopics.ORDER_CREATED,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.getPayload().getOrderId()
                        );
                    }

                });

    }
}