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
                "Please check your invoice which will be sent through email for payment details.\n\n" +
                "Thank you for choosing VSMS!";
        send(event.getUserEmail(), subject, body);
    }

    public void sendInvoiceGenerated(NotificationEvent event) {
        String subject = "Invoice Generated - VSMS";
        
        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Your invoice has been generated.\n\n");
        body.append("            INVOICE DETAILS\n");
        body.append("Invoice ID: ").append(event.getInvoiceId()).append("\n");
        body.append("Service ID: ").append(event.getServiceRequestId()).append("\n\n");
        body.append("PARTS USED:\n");
        
        if (event.getPartsUsed() != null && !event.getPartsUsed().isEmpty()) {
            for (NotificationEvent.PartDetail part : event.getPartsUsed()) {
                body.append(String.format("• %s\n", part.getPartName()));
                body.append(String.format("  Qty: %d × ₹%.2f = ₹%.2f\n\n", 
                    part.getQuantity(), part.getUnitPrice(), part.getTotalPrice()));
            }
        } else {
            body.append("No parts used\n\n");
        }
        
        body.append("BILLING SUMMARY:\n");
        
        Double partsTotal = event.getPartsTotal() != null ? event.getPartsTotal() : 0.0;
        Double labourCharges = event.getLabourCharges() != null ? event.getLabourCharges() : 0.0;
        Double totalAmount = event.getAmount() != null ? event.getAmount() : 0.0;
        
        body.append(String.format("Parts Total:    ₹%.2f\n", partsTotal));
        body.append(String.format("Labour Charges: ₹%.2f\n", labourCharges));
        body.append(String.format("TOTAL AMOUNT:   ₹%.2f\n", totalAmount));
        
        body.append("Payment Status: PENDING\n\n");
        body.append("Please complete the payment at your earliest convenience.\n\n");
        body.append("Thank you for choosing VSMS!");
        
        send(event.getUserEmail(), subject, body.toString());
    }

    public void sendRegistrationApproved(NotificationEvent event) {
        String subject = "Registration Approved - VSMS";
        String body = "Dear User,\n\n" +
                event.getMessage() + "\n\n" +
                "You can now login to the system.\n\n" +
                "Thank you for choosing VSMS!";
        send(event.getUserEmail(), subject, body);
    }

    public void sendRegistrationRejected(NotificationEvent event) {
        String subject = "Registration Update - VSMS";
        String body = "Dear User,\n\n" +
                event.getMessage() + "\n\n" +
                "If you have any questions, please contact support.\n\n" +
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
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
