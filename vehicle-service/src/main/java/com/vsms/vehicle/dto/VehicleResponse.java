package com.vsms.vehicle.dto;

import com.vsms.vehicle.entity.VehicleType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {

    private String id;
    private String userId;
    private String registrationNumber;
    private String brand;
    private String model;
    private VehicleType vehicleType;
    private Integer manufactureYear;
}
