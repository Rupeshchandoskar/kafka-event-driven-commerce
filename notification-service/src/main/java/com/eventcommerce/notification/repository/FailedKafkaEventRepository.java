package com.eventcommerce.notification.repository;

import com.eventcommerce.notification.entity.FailedKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedKafkaEventRepository extends JpaRepository<FailedKafkaEvent, String> {
}