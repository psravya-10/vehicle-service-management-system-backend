package com.vsms.servicerequest.controller;

import com.vsms.servicerequest.entity.Invoice;
import com.vsms.servicerequest.entity.PaymentStatus;
import com.vsms.servicerequest.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    @Mock
    private BillingService billingService;

    @InjectMocks
    private BillingController billingController;

    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
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
    void getByServiceRequest_ReturnsInvoice() {
        when(billingService.getByServiceRequest("sr-1")).thenReturn(testInvoice);

        ResponseEntity<Invoice> response = billingController.getByServiceRequest("sr-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("inv-1", response.getBody().getId());
    }


    @Test
    void getById_ReturnsInvoice() {
        when(billingService.getById("inv-1")).thenReturn(testInvoice);

        ResponseEntity<Invoice> response = billingController.getById("inv-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("inv-1", response.getBody().getId());
    }


    @Test
    void byStatus_ReturnsInvoices() {
        when(billingService.getByStatus(PaymentStatus.PENDING)).thenReturn(Arrays.asList(testInvoice));

        ResponseEntity<List<Invoice>> response = billingController.byStatus(PaymentStatus.PENDING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void byStatus_EmptyList() {
        when(billingService.getByStatus(PaymentStatus.PAID)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Invoice>> response = billingController.byStatus(PaymentStatus.PAID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }


    @Test
    void monthlyReport_ReturnsInvoices() {
        when(billingService.monthlyReport(2026, 1)).thenReturn(Arrays.asList(testInvoice));

        ResponseEntity<List<Invoice>> response = billingController.monthlyReport(2026, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }



    @Test
    void pay_Success() {
        doNothing().when(billingService).markInvoicePaid("inv-1");

        ResponseEntity<String> response = billingController.pay("inv-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment marked as PAID", response.getBody());
        verify(billingService).markInvoicePaid("inv-1");
    }
}
