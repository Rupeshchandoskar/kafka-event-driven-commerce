package com.eventcommerce.inventory.repository;

import com.eventcommerce.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {

    Optional<InventoryReservation> findByOrderId(String orderId);

}