package com.vsms.vehicle.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vsms.vehicle.dto.VehicleResponse;
import com.vsms.vehicle.service.VehicleService;

import java.util.List;

@RestController
@RequestMapping("/api/manager/vehicles")
@RequiredArgsConstructor
public class ManagerVehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }
}
