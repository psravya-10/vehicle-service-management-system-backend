package com.vsms.servicerequest.dto;

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
}

