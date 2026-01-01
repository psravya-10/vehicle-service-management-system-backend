package com.vsms.servicerequest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vsms.servicerequest.dto.AssignServiceRequestDto;
import com.vsms.servicerequest.dto.CreateServiceRequestDto;
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

    // CREATE SERVICE REQUEST
    public ServiceRequest create(CreateServiceRequestDto dto) {

        ServiceRequest request = ServiceRequest.builder()
                .userId(dto.getUserId())
                .vehicleId(dto.getVehicleId())
                .issueDescription(dto.getIssueDescription())
                .status(ServiceStatus.REQUESTED)
                .build();

        return requestRepo.save(request);
    }

    // ASSIGN SERVICE REQUEST 
//    @Transactional
    public void assign(String requestId, AssignServiceRequestDto dto) {

        System.out.println("Assign request started for requestId = " + requestId);

        ServiceRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        ServiceBay bay = bayRepo.findById(dto.getBayId())
                .orElseThrow(() -> new RuntimeException("Bay not found"));

        if (bay.getStatus() == BayStatus.BUSY) {
            throw new RuntimeException("Bay is busy");
        }

      
        System.out.println("Calling user-service to set technician BUSY: " + dto.getTechnicianId());

        //  FEIGN CALL
        userFeign.updateTechnicianStatus(dto.getTechnicianId(), false);

        System.out.println("Technician status updated successfully");

        bay.setStatus(BayStatus.BUSY);
        bayRepo.save(bay);

        req.setTechnicianId(dto.getTechnicianId());
        req.setBayId(dto.getBayId());
        req.setStatus(ServiceStatus.ASSIGNED);

        requestRepo.save(req);

        System.out.println("Service request assigned successfully");
    }

    public void startService(String requestId) {

        ServiceRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (req.getStatus() != ServiceStatus.ASSIGNED) {
            throw new RuntimeException("Service is not assigned");
        }

        req.setStatus(ServiceStatus.IN_PROGRESS);
        requestRepo.save(req);
    }


    public void completeService(String requestId) {

        ServiceRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (req.getStatus() != ServiceStatus.IN_PROGRESS) {
            throw new RuntimeException("Service is not in progress");
        }

        //  Release bay
        ServiceBay bay = bayRepo.findById(req.getBayId())
                .orElseThrow(() -> new RuntimeException("Bay not found"));

        bay.setStatus(BayStatus.AVAILABLE);
        bayRepo.save(bay);

        //  Release technician
        userFeign.updateTechnicianStatus(req.getTechnicianId(), true);

        // Update service request
        req.setStatus(ServiceStatus.COMPLETED);
        requestRepo.save(req);
    }


}
