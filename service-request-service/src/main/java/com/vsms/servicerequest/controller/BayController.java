package com.vsms.servicerequest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.entity.BayStatus;
import com.vsms.servicerequest.entity.ServiceBay;
import com.vsms.servicerequest.repository.ServiceBayRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bays")
@RequiredArgsConstructor
public class BayController {

    private final ServiceBayRepository repo;

    @GetMapping
    public List<ServiceBay> allBays() {
        return repo.findAll();
    }

    @GetMapping("/available")
    public List<ServiceBay> availableBays() {
        return repo.findByStatus(BayStatus.AVAILABLE);
    }
}
