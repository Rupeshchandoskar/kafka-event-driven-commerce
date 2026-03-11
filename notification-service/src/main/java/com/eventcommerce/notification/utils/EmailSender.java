package com.eventcommerce.notification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSender {

    public void sendEmail(String message) {

        log.info("Email Sent: {}", message);

    }

}