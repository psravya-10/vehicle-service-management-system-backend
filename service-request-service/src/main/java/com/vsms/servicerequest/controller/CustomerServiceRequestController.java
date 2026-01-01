package com.vsms.servicerequest.controller;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.CreateServiceRequestDto;
import com.vsms.servicerequest.dto.ServiceRequestResponse;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.service.ServiceRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/service-requests")
@RequiredArgsConstructor
public class CustomerServiceRequestController {

    private final ServiceRequestService service;

    @PostMapping
    public ServiceRequest create(@RequestBody CreateServiceRequestDto dto) {
        return service.create(dto);
    }
}

