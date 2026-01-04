package com.vsms.servicerequest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.entity.Invoice;
import com.vsms.servicerequest.entity.PaymentStatus;
import com.vsms.servicerequest.service.BillingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    
    @GetMapping("/service-request/{serviceRequestId}")
    public ResponseEntity<Invoice> getByServiceRequest(@PathVariable String serviceRequestId) {

        return ResponseEntity.ok(
                billingService.getByServiceRequest(serviceRequestId)
        );
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<Invoice> getById( @PathVariable String invoiceId) {

        return ResponseEntity.ok(
                billingService.getById(invoiceId)
        );
    }

    
    @GetMapping("/admin/invoices")
    public ResponseEntity<List<Invoice>> byStatus(@RequestParam PaymentStatus status) {

        return ResponseEntity.ok(
                billingService.getByStatus(status)
        );
    }

    @GetMapping("/admin/reports/monthly")
    public ResponseEntity<List<Invoice>> monthlyReport(@RequestParam int year, @RequestParam int month) {

        return ResponseEntity.ok(
                billingService.monthlyReport(year, month)
        );
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getUserInvoices(@PathVariable String userId) {

        return ResponseEntity.ok(
                billingService.getInvoicesByUser(userId)
        );
    }


    @PutMapping("/customer/invoices/{invoiceId}/pay")
    public ResponseEntity<String> pay(@PathVariable String invoiceId) {

        billingService.markInvoicePaid(invoiceId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Payment marked as PAID");
    }
}
