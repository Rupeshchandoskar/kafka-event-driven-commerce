package com.eventcommerce.common.event.payload;

import lombok.Data;

@Data
public class PaymentFailedPayload {

    private String orderId;
    private String reason;

}

