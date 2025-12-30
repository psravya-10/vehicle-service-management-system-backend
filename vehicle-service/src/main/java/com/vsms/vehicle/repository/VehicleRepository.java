package com.vsms.vehicle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vsms.vehicle.entity.Vehicle;

import java.util.List;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    List<Vehicle> findByUserId(String userId);
}
