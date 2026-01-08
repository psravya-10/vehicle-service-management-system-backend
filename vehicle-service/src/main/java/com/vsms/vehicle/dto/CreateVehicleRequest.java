package com.vsms.vehicle.dto;

import com.vsms.vehicle.entity.VehicleType;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateVehicleRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", message = "Invalid registration number format")
    private String registrationNumber;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 1990, message = "Manufacture year must be after 1990")
    @Max(value = 2099, message = "Manufacture year is invalid")
    private Integer manufactureYear;
}
