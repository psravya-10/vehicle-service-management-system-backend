package com.vsms.inventory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.inventory.dto.CreatePartRequestDto;
import com.vsms.inventory.entity.PartRequest;
import com.vsms.inventory.entity.RequestStatus;
import com.vsms.inventory.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory/requests")
@RequiredArgsConstructor
public class PartRequestController {

    private final InventoryService service;

    @GetMapping
    public List<PartRequest> all(@RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return service.getPartRequestsByStatus(status);
        }
        return service.getAllPartRequests();
    }

    @PostMapping
    public Map<String, String> requestPart(@Valid @RequestBody CreatePartRequestDto dto) {
        String id = service.requestPart(dto);
        return Map.of("requestId", id);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable String id, @RequestParam String managerId) {
        service.approveRequest(id, managerId);
        return ResponseEntity.ok("Part request approved successfully");
    }

    @GetMapping("/pending")
    public boolean hasPendingRequests(@RequestParam String serviceRequestId) {
        return service.hasPendingRequests(serviceRequestId);
    }

}

