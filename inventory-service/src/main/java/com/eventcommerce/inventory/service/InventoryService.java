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

        // Get product details from the payload (passed through the order)
        String productId = payload.getProductId();
        Integer quantity = payload.getQuantity();

        if (productId == null || quantity == null) {
            log.error("Product ID or quantity is missing in payload for orderId={}", payload.getOrderId());
            throw new RuntimeException("Product ID and quantity are required");
        }

        Inventory inventory = inventoryRepository
                .findById(productId)
                .orElseThrow(() -> {
                    log.error("Inventory not found for productId={}", productId);
                    return new RuntimeException("Inventory not found for product: " + productId);
                });

        if (inventory.getAvailableQuantity() < quantity) {
            log.error("Insufficient inventory for productId={}. Available={}, Requested={}", 
                    productId, inventory.getAvailableQuantity(), quantity);
            throw new RuntimeException("Insufficient inventory available");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);

        inventoryRepository.save(inventory);

        InventoryReservation reservation = new InventoryReservation();

        reservation.setOrderId(payload.getOrderId());
        reservation.setProductId(productId);
        reservation.setQuantity(quantity);
        reservation.setReservedAt(Instant.now());

        reservationRepository.save(reservation);

        log.info("Inventory reserved for orderId={} productId={} quantity={}", 
                payload.getOrderId(), productId, quantity);

        publishInventoryReservedEvent(payload.getOrderId(), productId, quantity);
    }

    private void publishInventoryReservedEvent(String orderId, String productId, Integer quantity) {

        InventoryReservedPayload payload = new InventoryReservedPayload();
        payload.setOrderId(orderId);
        payload.setProductId(productId);
        payload.setQuantity(quantity);

        BaseEvent<InventoryReservedPayload> event = new BaseEvent<>();

        event.setEventId(IdGenerator.generateEventId());
        event.setEventType("INVENTORY_RESERVED");
        event.setEventVersion("v1");
        event.setCreatedAt(Instant.now());
        event.setPayload(payload);

        producer.publishInventoryReserved(event);

        log.info("INVENTORY_RESERVED event published for orderId={}", orderId);
    }

    public void releaseReservedInventory(String orderId) {

        log.info("Releasing reserved inventory for orderId={}", orderId);

        InventoryReservation reservation = reservationRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Reservation not found for orderId={}", orderId);
                    return new RuntimeException("Reservation not found for order: " + orderId);
                });

        Inventory inventory = inventoryRepository
                .findById(reservation.getProductId())
                .orElseThrow(() -> {
                    log.error("Inventory not found for productId={}", reservation.getProductId());
                    return new RuntimeException("Inventory not found");
                });

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
        inventoryRepository.save(inventory);

        reservationRepository.delete(reservation);

        log.info("Inventory released for orderId={} productId={} quantity={}", 
                orderId, reservation.getProductId(), reservation.getQuantity());
    }
}