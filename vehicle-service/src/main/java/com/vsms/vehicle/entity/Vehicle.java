package com.vsms.vehicle.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    private String id;

    private String userId;

    private String registrationNumber;

    private String brand;

    private String model;

    private VehicleType vehicleType;

    private Integer manufactureYear;
}
