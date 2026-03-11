package com.eventcommerce.order.repository;

import com.eventcommerce.order.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, String> {

//    List<OutboxEvent> findByPublishedFalse();

    List<OutboxEvent> findTop50ByPublishedFalseOrderByCreatedAt();
}