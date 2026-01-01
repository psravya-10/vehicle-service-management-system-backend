package com.vsms.servicerequest.dto.feign;

import lombok.Data;

@Data
public class VehicleFeignResponse {

    private String id;
    private String userId;
    private String registrationNumber;
    private String brand;
    private String model;
    private String vehicleType;
    private Integer manufactureYear;
}
