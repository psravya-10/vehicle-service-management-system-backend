package com.vsms.servicerequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vsms.servicerequest.entity.Invoice;
import com.vsms.servicerequest.entity.PaymentStatus;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Optional<Invoice> findByServiceRequestId(String serviceRequestId);

    List<Invoice> findByPaymentStatus(PaymentStatus status);

    List<Invoice> findByCreatedAtBetween(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    );
    List<Invoice> findByUserId(String userId);
}
