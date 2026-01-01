package com.vsms.servicerequest.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;

public interface ServiceRequestRepository
        extends MongoRepository<ServiceRequest, String> {

    List<ServiceRequest> findByUserId(String userId);
    List<ServiceRequest> findByStatus(ServiceStatus status);

}
