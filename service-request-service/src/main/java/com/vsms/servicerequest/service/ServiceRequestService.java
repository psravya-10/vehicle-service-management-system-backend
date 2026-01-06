package com.vsms.servicerequest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vsms.servicerequest.dto.*;
import com.vsms.servicerequest.entity.*;

import com.vsms.servicerequest.messaging.NotificationPublisher;
import com.vsms.servicerequest.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository requestRepo;
    private final ServiceBayRepository bayRepo;
    private final UserServiceClient userServiceClient;
    private final  BillingService billingService;
    private final NotificationPublisher notificationPublisher;


    // customer - create
    public ServiceRequest create(CreateServiceRequestDto dto) {
        // Check if vehicle already has an active service request
        List<ServiceRequest> activeRequests = requestRepo.findByVehicleId(dto.getVehicleId());
        boolean hasActiveRequest = activeRequests.stream()
                .anyMatch(req -> req.getStatus() != ServiceStatus.CLOSED);
        
        if (hasActiveRequest) {
            throw new RuntimeException("Vehicle already in service");
        }
        
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
        userServiceClient.updateTechnicianStatus(dto.getTechnicianId(), false);

        req.setTechnicianId(dto.getTechnicianId());
        req.setBayId(dto.getBayId());
        req.setStatus(ServiceStatus.ASSIGNED);

        requestRepo.save(req);
        String userEmail = userServiceClient.getUserEmail(req.getUserId());

        notificationPublisher.publish(
            NotificationEvent.builder()
                .eventType("SERVICE_STARTED")
                .userId(req.getUserId())
                .userEmail(userEmail)
                .serviceRequestId(req.getId())
                .message("Your service has started")
                .build()
        );


    }

    // manger closeservice
    public void closeService(String requestId, double labourCharges) {

        ServiceRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (req.getStatus() != ServiceStatus.COMPLETED) {
            throw new RuntimeException("Service not completed yet");
        }

        if (labourCharges <= 0) {
            throw new RuntimeException("Labour charges required");
        }

        req.setLabourCharges(labourCharges);

        // release bay
        ServiceBay bay = bayRepo.findById(req.getBayId()).orElseThrow();
        bay.setStatus(BayStatus.AVAILABLE);
        bayRepo.save(bay);

        // release technician
        userServiceClient.updateTechnicianStatus(req.getTechnicianId(), true);

        req.setStatus(ServiceStatus.CLOSED);
        requestRepo.save(req);
        
        billingService.generateInvoice(requestId);
        
        String userEmail = userServiceClient.getUserEmail(req.getUserId());

        notificationPublisher.publish(
        	    NotificationEvent.builder()
        	        .eventType("SERVICE_CLOSED")
        	        .userEmail(userEmail)
        	        .serviceRequestId(req.getId())
        	        .message("Your service has been completed")
        	        .build()
        	);



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
    public void addUsedPart(String serviceRequestId, UsedPart usedPart) {

        ServiceRequest req = requestRepo.findById(serviceRequestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (req.getPartsUsed() == null) {
            req.setPartsUsed(new ArrayList<>());
        }

        req.getPartsUsed().add(usedPart);

        double partsTotal = req.getPartsUsed()
                .stream()
                .mapToDouble(UsedPart::getTotalPrice)
                .sum();

        req.setPartsTotal(partsTotal);

        requestRepo.save(req);
    }

    // Get service requests by vehicle ID
    public List<ServiceRequest> getByVehicleId(String vehicleId) {
        return requestRepo.findByVehicleId(vehicleId);
    }

    // Get service requests by user ID
    public List<ServiceRequest> getByUserId(String userId) {
        return requestRepo.findByUserId(userId);
    }

    // Get service requests by technician ID
    public List<ServiceRequest> getByTechnicianId(String technicianId) {
        return requestRepo.findByTechnicianId(technicianId);
    }

    // Save service request
    public ServiceRequest save(ServiceRequest request) {
        return requestRepo.save(request);
    }

    public long getTechnicianWorkload(String technicianId) {
        List<ServiceStatus> activeStatuses = List.of(
            ServiceStatus.ASSIGNED, 
            ServiceStatus.IN_PROGRESS
        );
        return requestRepo.countByTechnicianIdAndStatusIn(technicianId, activeStatuses);
    }
}

