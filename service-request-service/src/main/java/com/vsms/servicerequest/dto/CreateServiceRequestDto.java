package com.vsms.servicerequest.dto;

import com.vsms.servicerequest.entity.ServicePriority;

import lombok.Data;

@Data
public class CreateServiceRequestDto {

    private String userId;
    private String vehicleId;
    private String issueDescription;
    private ServicePriority priority;
}
