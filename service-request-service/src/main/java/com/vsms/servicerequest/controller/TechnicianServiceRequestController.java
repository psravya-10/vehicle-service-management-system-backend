package com.vsms.servicerequest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.TechnicianUpdateServiceStatusDto;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;
import com.vsms.servicerequest.service.ServiceRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/technician/service-requests")
@RequiredArgsConstructor
public class TechnicianServiceRequestController {

    private final ServiceRequestService service;

    @GetMapping
    public ResponseEntity<List<ServiceRequest>> getMyAssignedTasks(@RequestHeader("X-User-Id") String technicianId) {
        return ResponseEntity.ok(service.getByTechnicianId(technicianId));
    }

    @PutMapping("/{id}/status")
    public void updateStatus(
            @PathVariable String id,
            @RequestBody TechnicianUpdateServiceStatusDto dto) {

        ServiceRequest req = service.getById(id);

        if (dto.getStatus() != ServiceStatus.IN_PROGRESS &&
            dto.getStatus() != ServiceStatus.COMPLETED) {
            throw new RuntimeException("Invalid status update");
        }

        req.setStatus(dto.getStatus());
        req.setRemarks(dto.getRemarks());

        service.save(req);
    }
}
