package com.vsms.servicerequest.dto;

import com.vsms.servicerequest.entity.ServicePriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateServiceRequestDto {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    @NotNull(message = "Priority is required")
    private ServicePriority priority;
}
