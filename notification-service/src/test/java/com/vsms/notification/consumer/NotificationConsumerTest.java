package com.vsms.notification.consumer;

import com.vsms.notification.dto.NotificationEvent;
import com.vsms.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private NotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = NotificationEvent.builder()
                .userId("user-1")
                .userEmail("test@example.com")
                .serviceRequestId("sr-123")
                .build();
    }

    @Nested
    @DisplayName("consume Tests - Event Types")
    class ConsumeEventTypeTests {

        @Test
        @DisplayName("Should handle SERVICE_STARTED event")
        void consume_ServiceStartedEvent_CallsCorrectMethod() {
            testEvent.setEventType("SERVICE_STARTED");

            notificationConsumer.consume(testEvent);

            verify(emailService).sendServiceStarted(testEvent);
            verify(emailService, never()).sendServiceClosed(any());
            verify(emailService, never()).sendInvoiceGenerated(any());
        }

        @Test
        @DisplayName("Should handle SERVICE_CLOSED event")
        void consume_ServiceClosedEvent_CallsCorrectMethod() {
            testEvent.setEventType("SERVICE_CLOSED");

            notificationConsumer.consume(testEvent);

            verify(emailService).sendServiceClosed(testEvent);
            verify(emailService, never()).sendServiceStarted(any());
        }

        @Test
        @DisplayName("Should handle INVOICE_GENERATED event")
        void consume_InvoiceGeneratedEvent_CallsCorrectMethod() {
            testEvent.setEventType("INVOICE_GENERATED");
            testEvent.setInvoiceId("inv-456");
            testEvent.setAmount(1500.0);

            notificationConsumer.consume(testEvent);

            verify(emailService).sendInvoiceGenerated(testEvent);
        }

        @Test
        @DisplayName("Should handle REGISTRATION_APPROVED event")
        void consume_RegistrationApprovedEvent_CallsCorrectMethod() {
            testEvent.setEventType("REGISTRATION_APPROVED");
            testEvent.setMessage("Your registration has been approved!");

            notificationConsumer.consume(testEvent);

            verify(emailService).sendRegistrationApproved(testEvent);
        }

        @Test
        @DisplayName("Should handle REGISTRATION_REJECTED event")
        void consume_RegistrationRejectedEvent_CallsCorrectMethod() {
            testEvent.setEventType("REGISTRATION_REJECTED");
            testEvent.setMessage("Your registration has been rejected.");

            notificationConsumer.consume(testEvent);

            verify(emailService).sendRegistrationRejected(testEvent);
        }
    }

    @Nested
    @DisplayName("consume Tests - Edge Cases")
    class ConsumeEdgeCaseTests {

        @Test
        @DisplayName("Should handle unknown event type")
        void consume_UnknownEventType_NoEmailSent() {
            testEvent.setEventType("UNKNOWN_EVENT");

            notificationConsumer.consume(testEvent);

            verify(emailService, never()).sendServiceStarted(any());
            verify(emailService, never()).sendServiceClosed(any());
            verify(emailService, never()).sendInvoiceGenerated(any());
            verify(emailService, never()).sendRegistrationApproved(any());
            verify(emailService, never()).sendRegistrationRejected(any());
        }
    }
}
