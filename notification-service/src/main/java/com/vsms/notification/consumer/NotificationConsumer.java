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
            System.err.println("Invalid notification event: " + event);
            return; 
        }

        switch (event.getEventType()) {

            case "SERVICE_STARTED" -> {
                if (event.getUserEmail() != null)
                    emailService.sendServiceStarted(event);
            }

            case "SERVICE_CLOSED" -> {
                if (event.getUserEmail() != null)
                    emailService.sendServiceClosed(event);
            }

            case "INVOICE_GENERATED" -> {
                if (event.getUserEmail() != null)
                    emailService.sendInvoiceGenerated(event);
            }

            default ->
                System.out.println("Unknown event type: " + event.getEventType());
        }
    }

}

