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
@RequestMapping("/api/customer/vehicles")
@RequiredArgsConstructor
public class CustomerVehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle( @Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(@RequestHeader("X-User-Id") String userId) {
       
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable String id, @Valid @RequestBody UpdateVehicleRequest request) {

        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }
}
