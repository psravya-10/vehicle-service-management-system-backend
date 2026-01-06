package com.vsms.notification.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.vsms.notification.config.RabbitMQConfig;
import com.vsms.notification.dto.NotificationEvent;
import com.vsms.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(NotificationEvent event) {
        if (event == null || event.getEventType() == null) {
            log.error("Invalid notification event: {}", event);
            return;
        }

        log.info("Received event: {}", event.getEventType());

        switch (event.getEventType()) {
            case "SERVICE_STARTED" -> emailService.sendServiceStarted(event);
            case "SERVICE_CLOSED" -> emailService.sendServiceClosed(event);
            case "INVOICE_GENERATED" -> emailService.sendInvoiceGenerated(event);
            case "REGISTRATION_APPROVED" -> emailService.sendRegistrationApproved(event);
            case "REGISTRATION_REJECTED" -> emailService.sendRegistrationRejected(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
