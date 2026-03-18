package com.eventcommerce.payment.service;

import com.eventcommerce.common.enums.PaymentStatus;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.OrderCreatedPayload;
import com.eventcommerce.common.event.payload.PaymentSuccessPayload;
import com.eventcommerce.common.util.IdGenerator;
import com.eventcommerce.payment.entity.Payment;
import com.eventcommerce.payment.producer.PaymentEventProducer;
import com.eventcommerce.payment.repository.PaymentRepository;
import com.eventcommerce.payment.util.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer producer;
    private final PaymentProcessor paymentProcessor;

    public void processPayment(OrderCreatedPayload payload) {

        log.info(
                "Processing payment for orderId={} amount={}",
                payload.getOrderId(),
                payload.getAmount()
        );

        boolean success = paymentProcessor.processPayment(payload.getAmount());

        Payment payment = new Payment();
        payment.setOrderId(payload.getOrderId());
        payment.setAmount(BigDecimal.valueOf(payload.getAmount()));

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);

        log.info(
                "Payment processed for orderId={} status={}",
                payload.getOrderId(),
                payment.getStatus()
        );

        if (success) {

            PaymentSuccessPayload paymentPayload = new PaymentSuccessPayload();
            paymentPayload.setOrderId(payload.getOrderId());
            paymentPayload.setPaymentId(payment.getId());

            BaseEvent<PaymentSuccessPayload> event = new BaseEvent<>();

            event.setEventId(IdGenerator.generateEventId());
            event.setEventType("PAYMENT_SUCCESS");
            event.setEventVersion("v1");
            event.setCreatedAt(Instant.now());
            event.setPayload(paymentPayload);

            producer.publishPaymentSuccess(event);

            log.info(
                    "PaymentSuccessEvent published for orderId={}",
                    payload.getOrderId()
            );
        }

    }
}