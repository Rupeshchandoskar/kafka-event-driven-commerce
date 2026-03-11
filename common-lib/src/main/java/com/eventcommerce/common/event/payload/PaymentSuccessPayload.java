package com.eventcommerce.common.event.payload;

import lombok.Data;

@Data
public class PaymentSuccessPayload {

    private String orderId;
    private String paymentId;

}