package com.eventcommerce.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    private String userId;

    @NotNull
    @Positive
    private Double amount;

}