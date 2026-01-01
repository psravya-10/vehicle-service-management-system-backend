package com.vsms.inventory.repository;

import com.vsms.inventory.entity.PartRequest;
import com.vsms.inventory.entity.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PartRequestRepository extends MongoRepository<PartRequest, String> {
    List<PartRequest> findByStatus(RequestStatus status);
    List<PartRequest> findByServiceRequestId(String serviceRequestId);
}
