package com.eventcommerce.common.event.payload;

import lombok.Data;

@Data
public class InventoryReservedPayload {

    private String orderId;
    private String productId;
    private Integer quantity;

}