package com.vsms.vehicle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vsms.vehicle.dto.CreateVehicleRequest;
import com.vsms.vehicle.dto.UpdateVehicleRequest;
import com.vsms.vehicle.dto.VehicleResponse;
import com.vsms.vehicle.entity.Vehicle;
import com.vsms.vehicle.exception.VehicleNotFoundException;
import com.vsms.vehicle.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleResponse createVehicle(CreateVehicleRequest request) {

        // Check for duplicate registration number
        if (vehicleRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
            throw new RuntimeException("This registration number already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .userId(request.getUserId())
                .registrationNumber(request.getRegistrationNumber())
                .brand(request.getBrand())
                .model(request.getModel())
                .vehicleType(request.getVehicleType())
                .manufactureYear(request.getManufactureYear())
                .build();

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public VehicleResponse getVehicleById(String vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getVehiclesByUser(String userId) {

        return vehicleRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public VehicleResponse updateVehicle(String vehicleId, UpdateVehicleRequest request) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        if (request.getBrand() != null) vehicle.setBrand(request.getBrand());
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getVehicleType() != null) vehicle.setVehicleType(request.getVehicleType());
        if (request.getManufactureYear() != null) vehicle.setManufactureYear(request.getManufactureYear());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {

        return VehicleResponse.builder()
                .id(vehicle.getId())
                .userId(vehicle.getUserId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .vehicleType(vehicle.getVehicleType())
                .manufactureYear(vehicle.getManufactureYear())
                .build();
    }
}
