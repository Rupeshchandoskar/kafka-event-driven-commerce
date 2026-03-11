package com.eventcommerce.notification.service;

import com.eventcommerce.common.event.payload.InventoryReservedPayload;
import com.eventcommerce.notification.entity.Notification;
import com.eventcommerce.notification.repository.NotificationRepository;
import com.eventcommerce.notification.util.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailSender emailSender;

    public void sendNotification(InventoryReservedPayload payload) {

        String message = "Order " + payload.getOrderId() + " confirmed";

        log.info("Sending notification for orderId={}", payload.getOrderId());

        emailSender.sendEmail(message);

        Notification notification = new Notification();

        notification.setOrderId(payload.getOrderId());
        notification.setMessage(message);
        notification.setStatus("SENT");
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);

        log.info("Notification stored for orderId={}", payload.getOrderId());
    }

}