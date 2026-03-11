package com.eventcommerce.payment.repository;

import com.eventcommerce.payment.entity.FailedKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedKafkaEventRepository
        extends JpaRepository<FailedKafkaEvent, String> {
}