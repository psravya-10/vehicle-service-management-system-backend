package com.vsms.vehicle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vsms.vehicle.dto.CreateVehicleRequest;
import com.vsms.vehicle.dto.UpdateVehicleRequest;
import com.vsms.vehicle.dto.VehicleResponse;
import com.vsms.vehicle.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {

        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody UpdateVehicleRequest request) {

        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }
}
