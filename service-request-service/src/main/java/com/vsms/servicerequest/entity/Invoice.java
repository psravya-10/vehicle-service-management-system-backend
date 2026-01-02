package com.vsms.servicerequest.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    private String id;

    private String serviceRequestId;
    private String userId;
    private String vehicleId;

    private double labourCharges;
    private List<UsedPart> partsUsed;
    private double partsTotal;
    private double totalAmount;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
