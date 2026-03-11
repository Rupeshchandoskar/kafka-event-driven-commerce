package com.eventcommerce.inventory.repository;

import com.eventcommerce.inventory.entity.FailedKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedKafkaEventRepository extends JpaRepository<FailedKafkaEvent, String> {
}