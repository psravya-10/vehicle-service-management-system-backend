package com.vsms.servicerequest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.AssignServiceRequestDto;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.UsedPart;
import com.vsms.servicerequest.service.ServiceRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager/service-requests")
@RequiredArgsConstructor
public class ManagerServiceRequestController {

    private final ServiceRequestService service;
    
    @GetMapping
    public List<ServiceRequest> all() {
        return service.getAll();
    }

    @GetMapping("/pending")
    public List<ServiceRequest> pending() {
        return service.getPending();
    }
 // GET BY ID
    @GetMapping("/{id}")
    public ServiceRequest getById(@PathVariable String id) {
        return service.getById(id);
    }


    @PutMapping("/{id}/assign")
    public void assign(
            @PathVariable String id,
            @RequestBody AssignServiceRequestDto dto) {
        service.assign(id, dto);
    }
    @PutMapping("/{id}/close")
    public void close(
            @PathVariable String id,
            @RequestParam Double labourCharges) {

        service.closeService(id, labourCharges);
    }


}

