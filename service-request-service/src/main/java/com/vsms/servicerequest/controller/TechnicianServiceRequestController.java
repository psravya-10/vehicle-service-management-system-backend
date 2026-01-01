package com.vsms.servicerequest.controller;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.service.ServiceRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/technician/service-requests")
@RequiredArgsConstructor
public class TechnicianServiceRequestController {

    private final ServiceRequestService service;

    @PutMapping("/{id}/start")
    public void startService(@PathVariable String id) {
        service.startService(id);
    }

    @PutMapping("/{id}/complete")
    public void completeService(@PathVariable String id) {
        service.completeService(id);
    }
}

