package com.vsms.servicerequest.dto;

import lombok.Data;

@Data
public class CreateServiceRequestDto {

    private String userId;
    private String vehicleId;
    private String issueDescription;
}
