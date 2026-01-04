package com.vsms.servicerequest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.CreateServiceRequestDto;
import com.vsms.servicerequest.dto.CreateServiceRequestResponse;
import com.vsms.servicerequest.dto.ServiceRequestResponse;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;
import com.vsms.servicerequest.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/service-requests")
@RequiredArgsConstructor
public class CustomerServiceRequestController {

    private final ServiceRequestService service;

    @PostMapping
    public ResponseEntity<CreateServiceRequestResponse> create(
            @Valid @RequestBody CreateServiceRequestDto dto) {

        ServiceRequest created = service.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateServiceRequestResponse(created.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ServiceRequest>> getByVehicleId(@PathVariable String vehicleId) {
        return ResponseEntity.ok(service.getByVehicleId(vehicleId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<ServiceRequest>> getMyServiceRequests(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }
}
