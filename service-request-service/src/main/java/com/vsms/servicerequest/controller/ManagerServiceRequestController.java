package com.vsms.servicerequest.controller;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.AssignServiceRequestDto;
import com.vsms.servicerequest.service.ServiceRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager/service-requests")
@RequiredArgsConstructor
public class ManagerServiceRequestController {

    private final ServiceRequestService service;

    @PutMapping("/{id}/assign")
    public void assign(
            @PathVariable String id,
            @RequestBody AssignServiceRequestDto dto) {
        service.assign(id, dto);
    }
}

