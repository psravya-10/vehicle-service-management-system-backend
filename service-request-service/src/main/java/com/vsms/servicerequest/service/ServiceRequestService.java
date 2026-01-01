package com.vsms.servicerequest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vsms.servicerequest.dto.*;
import com.vsms.servicerequest.entity.*;
import com.vsms.servicerequest.feign.UserFeignClient;
import com.vsms.servicerequest.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository requestRepo;
    private final ServiceBayRepository bayRepo;
    private final UserFeignClient userFeign;

    // customer - create
    public ServiceRequest create(CreateServiceRequestDto dto) {
        return requestRepo.save(
            ServiceRequest.builder()
                .userId(dto.getUserId())
                .vehicleId(dto.getVehicleId())
                .issueDescription(dto.getIssueDescription())
                .priority(dto.getPriority())
                .status(ServiceStatus.REQUESTED)
                .build()
        );
    }

    // manger assigning task
    public void assign(String requestId, AssignServiceRequestDto dto) {

        ServiceRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (req.getStatus() != ServiceStatus.REQUESTED) {
            throw new RuntimeException("Service already assigned");
        }

        ServiceBay bay = bayRepo.findById(dto.getBayId())
                .orElseThrow(() -> new RuntimeException("Bay not found"));

        if (bay.getStatus() != BayStatus.AVAILABLE) {
            throw new RuntimeException("Bay is busy");
        }

        // Occupy bay
        bay.setStatus(BayStatus.BUSY);
        bayRepo.save(bay);

        // Occupy technician
        userFeign.updateTechnicianStatus(dto.getTechnicianId(), false);

        req.setTechnicianId(dto.getTechnicianId());
        req.setBayId(dto.getBayId());
        req.setStatus(ServiceStatus.ASSIGNED);

        requestRepo.save(req);
    }

    // manger closeservice
    public void closeService(String requestId) {

        ServiceRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (req.getStatus() != ServiceStatus.COMPLETED) {
            throw new RuntimeException("Service not completed yet");
        }

        // Release bay
        ServiceBay bay = bayRepo.findById(req.getBayId()).orElseThrow();
        bay.setStatus(BayStatus.AVAILABLE);
        bayRepo.save(bay);

        // Release technician
        userFeign.updateTechnicianStatus(req.getTechnicianId(), true);

        req.setStatus(ServiceStatus.CLOSED);
        requestRepo.save(req);
    }
    public List<ServiceRequest> getAll() {
        return requestRepo.findAll();
    }

    public List<ServiceRequest> getPending() {
        return requestRepo.findByStatus(ServiceStatus.REQUESTED);
    }
    public ServiceRequest getById(String id) {
        return requestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service request not found"));
    }

    
}
