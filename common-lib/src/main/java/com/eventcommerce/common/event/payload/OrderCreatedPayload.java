package com.eventcommerce.common.event.payload;

import lombok.Data;

@Data
public class OrderCreatedPayload {

    private String orderId;
    private String userId;
    private Double amount;

}