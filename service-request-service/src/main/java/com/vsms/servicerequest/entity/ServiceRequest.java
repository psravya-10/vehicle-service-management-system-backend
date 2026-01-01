package com.vsms.servicerequest.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "service_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {

    @Id
    private String id;

    private String userId;
    private String vehicleId;
    private String issueDescription;

    private String technicianId;
    private String bayId;

    private ServiceStatus status;
}

