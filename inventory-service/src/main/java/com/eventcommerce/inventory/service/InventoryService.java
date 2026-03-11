package com.eventcommerce.inventory.service;

import com.eventcommerce.common.event.BaseEvent;
import com.eventcommerce.common.event.payload.InventoryReservedPayload;
import com.eventcommerce.common.event.payload.PaymentSuccessPayload;
import com.eventcommerce.common.util.IdGenerator;
import com.eventcommerce.inventory.entity.Inventory;
import com.eventcommerce.inventory.entity.InventoryReservation;
import com.eventcommerce.inventory.producer.InventoryEventProducer;
import com.eventcommerce.inventory.repository.InventoryRepository;
import com.eventcommerce.inventory.repository.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final InventoryEventProducer producer;

    public void reserveInventory(PaymentSuccessPayload payload) {

        log.info("Reserving inventory for orderId={}", payload.getOrderId());

        Inventory inventory = inventoryRepository
                .findById("product-1")
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        if (inventory.getAvailableQuantity() <= 0) {
            log.error("Inventory not available for product-1");
            throw new RuntimeException("Inventory not available");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - 1);

        inventoryRepository.save(inventory);

        InventoryReservation reservation = new InventoryReservation();

        reservation.setOrderId(payload.getOrderId());
        reservation.setProductId("product-1");
        reservation.setQuantity(1);
        reservation.setReservedAt(Instant.now());

        reservationRepository.save(reservation);

        log.info("Inventory reserved for orderId={}", payload.getOrderId());

        publishInventoryReservedEvent(payload.getOrderId());
    }

    private void publishInventoryReservedEvent(String orderId) {

        InventoryReservedPayload payload = new InventoryReservedPayload();
        payload.setOrderId(orderId);
        payload.setProductId("product-1");
        payload.setQuantity(1);

        BaseEvent<InventoryReservedPayload> event = new BaseEvent<>();

        event.setEventId(IdGenerator.generateEventId());
        event.setEventType("INVENTORY_RESERVED");
        event.setEventVersion("v1");
        event.setCreatedAt(Instant.now());
        event.setPayload(payload);

        producer.publishInventoryReserved(event);

        log.info("INVENTORY_RESERVED event published for orderId={}", orderId);
    }
}