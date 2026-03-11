package com.eventcommerce.order.scheduler;

import com.eventcommerce.order.service.OutboxPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxPublisherService publisherService;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {

        publisherService.publishEvents();

    }

}