package com.eventcommerce.payment.entity;

import com.eventcommerce.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String orderId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

}