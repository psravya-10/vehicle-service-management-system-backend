package com.vsms.servicerequest.controller;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.TechnicianUpdateServiceStatusDto;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;
import com.vsms.servicerequest.repository.ServiceRequestRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/technician/service-requests")
@RequiredArgsConstructor
public class TechnicianServiceRequestController {

    private final ServiceRequestRepository repo;

    @PutMapping("/{id}/status")
    public void updateStatus(
            @PathVariable String id,
            @RequestBody TechnicianUpdateServiceStatusDto dto) {

        ServiceRequest req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (dto.getStatus() != ServiceStatus.IN_PROGRESS &&
            dto.getStatus() != ServiceStatus.COMPLETED) {
            throw new RuntimeException("Invalid status update");
        }

        req.setStatus(dto.getStatus());
        req.setRemarks(dto.getRemarks());

        repo.save(req);
    }
}
