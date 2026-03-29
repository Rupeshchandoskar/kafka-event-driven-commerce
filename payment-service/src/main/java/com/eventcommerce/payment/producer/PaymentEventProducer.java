package com.eventcommerce.payment.producer;

import com.eventcommerce.common.constants.KafkaTopics;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.PaymentFailedPayload;
import com.eventcommerce.common.event.payload.PaymentSuccessPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccess(BaseEvent<PaymentSuccessPayload> event) {

        kafkaTemplate.send(KafkaTopics.PAYMENT_SUCCESS, event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error(
                                "Failed to publish PAYMENT_SUCCESS event | eventId={} orderId={}",
                                event.getEventId(),
                                event.getPayload().getOrderId(),
                                ex
                        );
                    } else {
                        log.info(
                                "PAYMENT_SUCCESS event published | topic={} partition={} offset={} orderId={}",
                                KafkaTopics.PAYMENT_SUCCESS,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.getPayload().getOrderId()
                        );
                    }

                });
    }

    public void publishPaymentFailed(BaseEvent<PaymentFailedPayload> event) {

        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error(
                                "Failed to publish PAYMENT_FAILED event | eventId={} orderId={}",
                                event.getEventId(),
                                event.getPayload().getOrderId(),
                                ex
                        );
                    } else {
                        log.info(
                                "PAYMENT_FAILED event published | topic={} partition={} offset={} orderId={}",
                                KafkaTopics.PAYMENT_FAILED,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.getPayload().getOrderId()
                        );
                    }

                });
    }
}