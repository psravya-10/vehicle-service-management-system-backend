package com.vsms.vehicle.dto;

import com.vsms.vehicle.entity.VehicleType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateVehicleRequest {

    private String brand;

    private String model;

    private VehicleType vehicleType;

    @Min(value = 1990, message = "Manufacture year must be after 1990")
    @Max(value = 2099, message = "Manufacture year is invalid")
    private Integer manufactureYear;
}

