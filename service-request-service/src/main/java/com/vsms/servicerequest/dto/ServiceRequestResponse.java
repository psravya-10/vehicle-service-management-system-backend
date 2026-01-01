package com.vsms.servicerequest.dto;

import com.vsms.servicerequest.entity.ServiceStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceRequestResponse {
    private String id;
    private String status;
    private String technicianId;
    private String bayId;
}

