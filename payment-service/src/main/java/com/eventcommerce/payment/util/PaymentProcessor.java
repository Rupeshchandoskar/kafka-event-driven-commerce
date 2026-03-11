package com.eventcommerce.payment.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PaymentProcessor {

    public boolean processPayment(Double amount) {

        Random random = new Random();

        return random.nextInt(10) > 2;

    }

}