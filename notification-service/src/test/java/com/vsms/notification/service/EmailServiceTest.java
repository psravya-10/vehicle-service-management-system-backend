package com.vsms.notification.service;

import com.vsms.notification.dto.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private NotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = NotificationEvent.builder()
                .eventType("SERVICE_STARTED")
                .userId("user-1")
                .userEmail("test@example.com")
                .serviceRequestId("sr-123")
                .invoiceId("inv-456")
                .amount(1500.0)
                .message("Test message")
                .labourCharges(500.0)
                .partsTotal(1000.0)
                .build();
    }

    @Nested
    @DisplayName("sendServiceStarted Tests")
    class SendServiceStartedTests {

        @Test
        @DisplayName("Should send service started email successfully")
        void sendServiceStarted_Success() {
            emailService.sendServiceStarted(testEvent);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());

            SimpleMailMessage message = captor.getValue();
            assertEquals("test@example.com", message.getTo()[0]);
            assertEquals("Service Started - VSMS", message.getSubject());
            assertTrue(message.getText().contains("sr-123"));
        }
    }

    @Nested
    @DisplayName("sendServiceClosed Tests")
    class SendServiceClosedTests {

        @Test
        @DisplayName("Should send service closed email successfully")
        void sendServiceClosed_Success() {
            emailService.sendServiceClosed(testEvent);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());

            SimpleMailMessage message = captor.getValue();
            assertEquals("test@example.com", message.getTo()[0]);
            assertEquals("Service Completed - VSMS", message.getSubject());
            assertTrue(message.getText().contains("sr-123"));
        }
    }

    @Nested
    @DisplayName("sendInvoiceGenerated Tests")
    class SendInvoiceGeneratedTests {

        @Test
        @DisplayName("Should send invoice email with parts successfully")
        void sendInvoiceGenerated_WithParts_Success() {
            NotificationEvent.PartDetail part1 = NotificationEvent.PartDetail.builder()
                    .partName("Brake Pad")
                    .quantity(2)
                    .unitPrice(250.0)
                    .totalPrice(500.0)
                    .build();
            testEvent.setPartsUsed(Arrays.asList(part1));

            emailService.sendInvoiceGenerated(testEvent);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());

            SimpleMailMessage message = captor.getValue();
            assertEquals("Invoice Generated - VSMS", message.getSubject());
            assertTrue(message.getText().contains("Brake Pad"));
            assertTrue(message.getText().contains("inv-456"));
        }
    }

    @Nested
    @DisplayName("sendRegistrationApproved Tests")
    class SendRegistrationApprovedTests {

        @Test
        @DisplayName("Should send registration approved email successfully")
        void sendRegistrationApproved_Success() {
            testEvent.setMessage("Your registration has been approved!");

            emailService.sendRegistrationApproved(testEvent);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());

            SimpleMailMessage message = captor.getValue();
            assertEquals("Registration Approved - VSMS", message.getSubject());
            assertTrue(message.getText().contains("Your registration has been approved!"));
        }
    }

    @Nested
    @DisplayName("sendRegistrationRejected Tests")
    class SendRegistrationRejectedTests {

        @Test
        @DisplayName("Should send registration rejected email successfully")
        void sendRegistrationRejected_Success() {
            testEvent.setMessage("Your registration has been rejected.");

            emailService.sendRegistrationRejected(testEvent);

            ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mailSender).send(captor.capture());

            SimpleMailMessage message = captor.getValue();
            assertEquals("Registration Update - VSMS", message.getSubject());
            assertTrue(message.getText().contains("Your registration has been rejected."));
        }
    }
}
