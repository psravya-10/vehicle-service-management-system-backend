package com.vsms.servicerequest.dto;

import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventType;        
    private String userId;           
    private String userEmail;        
    private String serviceRequestId;
    private String invoiceId;
    private Double amount;
    private String message;
    
    // Invoice details for detailed email
    private Double labourCharges;
    private Double partsTotal;
    private List<PartDetail> partsUsed;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartDetail {
        private String partName;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
    }
}
