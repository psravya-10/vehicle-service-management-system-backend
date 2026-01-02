package com.vsms.servicerequest.controller;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.entity.UsedPart;
import com.vsms.servicerequest.service.ServiceRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/service-requests")
@RequiredArgsConstructor
public class InternalServiceRequestController {

    private final ServiceRequestService service;

    @PostMapping("/{id}/parts")
    public void addUsedPart(
            @PathVariable String id,
            @RequestBody UsedPart usedPart) {

        service.addUsedPart(id, usedPart);
    }
}
