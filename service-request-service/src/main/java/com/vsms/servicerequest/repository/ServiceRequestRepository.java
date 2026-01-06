package com.vsms.servicerequest.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;

public interface ServiceRequestRepository
        extends MongoRepository<ServiceRequest, String> {

    List<ServiceRequest> findByUserId(String userId);
    List<ServiceRequest> findByStatus(ServiceStatus status);
    List<ServiceRequest> findByVehicleId(String vehicleId);
    List<ServiceRequest> findByTechnicianId(String technicianId);
    List<ServiceRequest> findByTechnicianIdAndStatusIn(String technicianId, List<ServiceStatus> statuses);
    long countByTechnicianIdAndStatusIn(String technicianId, List<ServiceStatus> statuses);

}

