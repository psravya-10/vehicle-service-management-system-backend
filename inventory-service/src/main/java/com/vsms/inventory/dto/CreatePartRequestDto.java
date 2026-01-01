package com.vsms.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePartRequestDto {

    @NotBlank(message = "Service request ID is required")
    private String serviceRequestId;

    @NotBlank(message = "Technician ID is required")
    private String technicianId;

    @NotBlank(message = "Part ID is required")
    private String partId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}

