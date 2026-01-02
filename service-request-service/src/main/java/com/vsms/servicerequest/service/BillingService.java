package com.vsms.servicerequest.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vsms.servicerequest.dto.NotificationEvent;
import com.vsms.servicerequest.entity.*;
import com.vsms.servicerequest.exception.BusinessException;
import com.vsms.servicerequest.feign.UserFeignClient;
import com.vsms.servicerequest.messaging.NotificationPublisher;
import com.vsms.servicerequest.repository.InvoiceRepository;
import com.vsms.servicerequest.repository.ServiceRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final ServiceRequestRepository serviceRepo;
    private final InvoiceRepository invoiceRepo;
    private final NotificationPublisher notificationPublisher;
    private final UserFeignClient userFeign;

    
    public Invoice generateInvoice(String serviceRequestId) {

        ServiceRequest req = serviceRepo.findById(serviceRequestId)
                .orElseThrow(() -> new BusinessException("Service request not found"));

        if (req.getStatus() != ServiceStatus.CLOSED) {
            throw new BusinessException("Service must be CLOSED to generate invoice");
        }

        // Prevent duplicate invoice
        invoiceRepo.findByServiceRequestId(serviceRequestId)
                .ifPresent(i -> {
                    throw new BusinessException("Invoice already generated");
                });

        double labour = req.getLabourCharges();
        double parts = req.getPartsTotal() == null ? 0 : req.getPartsTotal();

        Invoice invoice = Invoice.builder()
                .serviceRequestId(req.getId())
                .userId(req.getUserId())
                .vehicleId(req.getVehicleId())
                .labourCharges(labour)
                .partsUsed(req.getPartsUsed())
                .partsTotal(parts)
                .totalAmount(labour + parts)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Invoice savedInvoice = invoiceRepo.save(invoice);
        
        // Send invoice notification when invoice is generated (during close)
        String userEmail = userFeign.getUserEmail(req.getUserId());
        notificationPublisher.publish(
            NotificationEvent.builder()
                .eventType("INVOICE_GENERATED")
                .userEmail(userEmail)
                .serviceRequestId(serviceRequestId)
                .invoiceId(savedInvoice.getId())
                .amount(savedInvoice.getTotalAmount())
                .build()
        );
        
        return savedInvoice;
    }

   
    public void markInvoicePaid(String invoiceId) {

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new BusinessException("Invoice not found"));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessException("Invoice already paid");
        }

        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        invoiceRepo.save(invoice);
    }

    // Get invoice by service request
    public Invoice getByServiceRequest(String serviceRequestId) {
        return invoiceRepo.findByServiceRequestId(serviceRequestId)
                .orElseThrow(() -> new BusinessException("Invoice not found"));
    }

    public Invoice getById(String invoiceId) {
        return invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new BusinessException("Invoice not found"));
    }

  
    public List<Invoice> getByStatus(PaymentStatus status) {
        return invoiceRepo.findByPaymentStatus(status);
    }


    public List<Invoice> monthlyReport(int year, int month) {

        if (month < 1 || month > 12) {
            throw new BusinessException("Month must be between 1 and 12");
        }

        YearMonth ym = YearMonth.of(year, month);

        return invoiceRepo.findByCreatedAtBetween(
                ym.atDay(1).atStartOfDay(),
                ym.atEndOfMonth().atTime(23, 59, 59)
        );
    }

    
    public List<Invoice> getInvoicesByUser(String userId) {
        return invoiceRepo.findByUserId(userId);
    }
}
