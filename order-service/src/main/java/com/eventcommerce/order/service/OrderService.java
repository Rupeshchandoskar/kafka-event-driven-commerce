package com.eventcommerce.order.service;

import com.eventcommerce.common.enums.OrderStatus;
import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.OrderCreatedPayload;
import com.eventcommerce.common.util.IdGenerator;
import com.eventcommerce.order.dto.OrderRequest;
import com.eventcommerce.order.entity.Order;
import com.eventcommerce.order.entity.OutboxEvent;
import com.eventcommerce.order.repository.OrderRepository;
import com.eventcommerce.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(OrderRequest request) {

        log.info("Creating order for userId={} amount={}",
                request.getUserId(),
                request.getAmount());

        Order order = new Order();

        order.setUserId(request.getUserId());
        order.setAmount(BigDecimal.valueOf(request.getAmount()));
        order.setStatus(OrderStatus.CREATED);

        order = orderRepository.save(order);

        log.info("Order saved orderId={}", order.getId());

        saveOrderCreatedEvent(order);

        return order;
    }

    private void saveOrderCreatedEvent(Order order) {

        try {

            OrderCreatedPayload payload = new OrderCreatedPayload();

            payload.setOrderId(order.getId());
            payload.setUserId(order.getUserId());
            payload.setAmount(order.getAmount().doubleValue());

            BaseEvent<OrderCreatedPayload> event = new BaseEvent<>();

            event.setEventId(IdGenerator.generateEventId());
            event.setEventType("ORDER_CREATED");
            event.setEventVersion("v1");
            event.setCreatedAt(Instant.now());
            event.setPayload(payload);

            OutboxEvent outbox = new OutboxEvent();

            outbox.setId(event.getEventId());
            outbox.setEventType(event.getEventType());
            outbox.setAggregateId(order.getId());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setCreatedAt(Instant.now());
            outbox.setPublished(false);

            outboxRepository.save(outbox);

            log.info("Outbox event stored eventId={}", event.getEventId());

        } catch (Exception e) {

            log.error("Failed creating outbox event", e);
            throw new RuntimeException(e);

        }

    }

}