package com.eventcommerce.inventory.repository;

import com.eventcommerce.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
}