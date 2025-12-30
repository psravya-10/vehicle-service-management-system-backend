package com.vsms.vehicle.service;


import java.util.List;

import com.vsms.vehicle.dto.CreateVehicleRequest;
import com.vsms.vehicle.dto.UpdateVehicleRequest;
import com.vsms.vehicle.dto.VehicleResponse;

public interface VehicleService {

    VehicleResponse createVehicle(CreateVehicleRequest request);

    List<VehicleResponse> getAllVehicles();

    VehicleResponse getVehicleById(String vehicleId);

    List<VehicleResponse> getVehiclesByUser(String userId);

    VehicleResponse updateVehicle(String vehicleId, UpdateVehicleRequest request);
}
