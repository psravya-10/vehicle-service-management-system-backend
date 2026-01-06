package com.vsms.servicerequest.service;

import com.vsms.servicerequest.dto.NotificationEvent;
import com.vsms.servicerequest.entity.*;
import com.vsms.servicerequest.exception.BusinessException;
import com.vsms.servicerequest.messaging.NotificationPublisher;
import com.vsms.servicerequest.repository.InvoiceRepository;
import com.vsms.servicerequest.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private ServiceRequestRepository serviceRepo;

    @Mock
    private InvoiceRepository invoiceRepo;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private BillingService billingService;

    private ServiceRequest testRequest;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        testRequest = ServiceRequest.builder()
                .id("sr-1")
                .userId("user-1")
                .vehicleId("vehicle-1")
                .issueDescription("Test issue")
                .status(ServiceStatus.CLOSED)
                .labourCharges(100.0)
                .partsTotal(50.0)
                .build();

        testInvoice = Invoice.builder()
                .id("inv-1")
                .serviceRequestId("sr-1")
                .userId("user-1")
                .vehicleId("vehicle-1")
                .labourCharges(100.0)
                .partsTotal(50.0)
                .totalAmount(150.0)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void generateInvoice_Success() {
        when(serviceRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(invoiceRepo.findByServiceRequestId("sr-1")).thenReturn(Optional.empty());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(testInvoice);
        when(userServiceClient.getUserEmail(anyString())).thenReturn("test@example.com");

        Invoice result = billingService.generateInvoice("sr-1");

        assertNotNull(result);
        assertEquals("inv-1", result.getId());
        verify(invoiceRepo).save(any(Invoice.class));
        verify(notificationPublisher).publish(any(NotificationEvent.class));
    }

    @Test
    void generateInvoice_WithPartsUsed() {
        UsedPart part = UsedPart.builder()
                .partName("Oil Filter")
                .quantity(2)
                .unitPrice(25.0)
                .totalPrice(50.0)
                .build();
        testRequest.setPartsUsed(Arrays.asList(part));
        
        when(serviceRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(invoiceRepo.findByServiceRequestId("sr-1")).thenReturn(Optional.empty());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(testInvoice);
        when(userServiceClient.getUserEmail(anyString())).thenReturn("test@example.com");

        Invoice result = billingService.generateInvoice("sr-1");

        assertNotNull(result);
        verify(notificationPublisher).publish(any(NotificationEvent.class));
    }

    @Test
    void generateInvoice_RequestNotFound_ThrowsException() {
        when(serviceRepo.findById("sr-1")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> billingService.generateInvoice("sr-1"));
    }

    @Test
    void generateInvoice_ServiceNotClosed_ThrowsException() {
        testRequest.setStatus(ServiceStatus.COMPLETED);
        when(serviceRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));

        assertThrows(BusinessException.class, () -> billingService.generateInvoice("sr-1"));
    }

    @Test
    void generateInvoice_AlreadyExists_ThrowsException() {
        when(serviceRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(invoiceRepo.findByServiceRequestId("sr-1")).thenReturn(Optional.of(testInvoice));

        assertThrows(BusinessException.class, () -> billingService.generateInvoice("sr-1"));
    }

    @Test
    void generateInvoice_NullPartsTotal() {
        testRequest.setPartsTotal(null);
        when(serviceRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(invoiceRepo.findByServiceRequestId("sr-1")).thenReturn(Optional.empty());
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(testInvoice);
        when(userServiceClient.getUserEmail(anyString())).thenReturn("test@example.com");

        Invoice result = billingService.generateInvoice("sr-1");

        assertNotNull(result);
    }


    @Test
    void markInvoicePaid_Success() {
        when(invoiceRepo.findById("inv-1")).thenReturn(Optional.of(testInvoice));
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(testInvoice);

        billingService.markInvoicePaid("inv-1");

        verify(invoiceRepo).save(any(Invoice.class));
    }

    @Test
    void markInvoicePaid_NotFound_ThrowsException() {
        when(invoiceRepo.findById("inv-1")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> billingService.markInvoicePaid("inv-1"));
    }

    @Test
    void markInvoicePaid_AlreadyPaid_ThrowsException() {
        testInvoice.setPaymentStatus(PaymentStatus.PAID);
        when(invoiceRepo.findById("inv-1")).thenReturn(Optional.of(testInvoice));

        assertThrows(BusinessException.class, () -> billingService.markInvoicePaid("inv-1"));
    }


    @Test
    void getByServiceRequest_Found() {
        when(invoiceRepo.findByServiceRequestId("sr-1")).thenReturn(Optional.of(testInvoice));

        Invoice result = billingService.getByServiceRequest("sr-1");

        assertEquals("inv-1", result.getId());
    }

    @Test
    void getByServiceRequest_NotFound_ThrowsException() {
        when(invoiceRepo.findByServiceRequestId("sr-1")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> billingService.getByServiceRequest("sr-1"));
    }


    @Test
    void getById_Found() {
        when(invoiceRepo.findById("inv-1")).thenReturn(Optional.of(testInvoice));

        Invoice result = billingService.getById("inv-1");

        assertEquals("inv-1", result.getId());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(invoiceRepo.findById("inv-1")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> billingService.getById("inv-1"));
    }


    @Test
    void getByStatus_ReturnsInvoices() {
        when(invoiceRepo.findByPaymentStatus(PaymentStatus.PENDING)).thenReturn(Arrays.asList(testInvoice));

        List<Invoice> result = billingService.getByStatus(PaymentStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void getByStatus_EmptyList() {
        when(invoiceRepo.findByPaymentStatus(PaymentStatus.PAID)).thenReturn(Collections.emptyList());

        List<Invoice> result = billingService.getByStatus(PaymentStatus.PAID);

        assertTrue(result.isEmpty());
    }

    @Test
    void monthlyReport_Success() {
        when(invoiceRepo.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testInvoice));

        List<Invoice> result = billingService.monthlyReport(2026, 1);

        assertEquals(1, result.size());
    }



}
