package com.eventcommerce.order.controller;

import com.eventcommerce.order.dto.OrderRequest;
import com.eventcommerce.order.entity.Order;
import com.eventcommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {

        log.info(
                "Received order request | userId={} amount={}",
                request.getUserId(),
                request.getAmount()
        );

        Order order = orderService.createOrder(request);

        log.info("Order created successfully | orderId={}", order.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }

}