package com.eventcommerce.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    private String userId;

    @NotNull
    private String productId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private Double amount;

}