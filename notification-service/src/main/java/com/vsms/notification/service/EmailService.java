package com.vsms.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.vsms.notification.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;

    public void sendServiceStarted(NotificationEvent event) {
        String subject = "Service Started - VSMS";
        String body = "Dear Customer,\n\n" +
                "Your vehicle service has started.\n" +
                "Service ID: " + event.getServiceRequestId() + "\n\n" +
                "We will notify you when the service is completed.\n\n" +
                "Thank you for choosing VSMS!";
        send(event.getUserEmail(), subject, body);
    }

    public void sendServiceClosed(NotificationEvent event) {
        String subject = "Service Completed - VSMS";
        String body = "Dear Customer,\n\n" +
                "Your vehicle service has been completed.\n" +
                "Service ID: " + event.getServiceRequestId() + "\n\n" +
                "Please check your invoice which will be send thorugh emailfor payment details.\n\n" +
                "Thank you for choosing VSMS!";
        send(event.getUserEmail(), subject, body);
    }

    public void sendInvoiceGenerated(NotificationEvent event) {
        String subject = "Invoice Generated - VSMS";
        String body = "Dear Customer,\n\n" +
                "Your invoice has been generated.\n" +
                "Invoice ID: " + event.getInvoiceId() + "\n" +
                "Amount: ₹" + (event.getAmount() != null ? event.getAmount() : 0) + "\n" +
                "Payment Status: PENDING\n\n" +
                "Please complete the payment at your earliest convenience.\n\n" +
                "Thank you for choosing VSMS!";
        send(event.getUserEmail(), subject, body);
    }

    private void send(String to, String subject, String body) {
        if (to == null || to.isEmpty()) {
            log.error("Cannot send email: recipient email is null or empty");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
         
        }
    }
}

