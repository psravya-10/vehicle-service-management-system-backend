package com.vsms.inventory.service;

import org.springframework.stereotype.Service;

import com.vsms.inventory.dto.UsedPartDto;
import com.vsms.inventory.exception.ServiceUnavailableException;
import com.vsms.inventory.feign.ServiceRequestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRequestServiceClient {

    private final ServiceRequestClient serviceRequestClient;

    @CircuitBreaker(name = "serviceRequestService", fallbackMethod = "getServiceRequestByIdFallback")
    public Object getServiceRequestById(String id) {
        return serviceRequestClient.getServiceRequestById(id);
    }

    public Object getServiceRequestByIdFallback(String id, Exception e) {
        log.error("Service Request Service is unavailable. Cannot get service request details.", e);
        throw new ServiceUnavailableException("Service Request Service");
    }

    @CircuitBreaker(name = "serviceRequestService", fallbackMethod = "addUsedPartFallback")
    public void addUsedPart(String id, UsedPartDto dto) {
        serviceRequestClient.addUsedPart(id, dto);
    }

    public void addUsedPartFallback(String id, UsedPartDto dto, Exception e) {
        log.error("Service Request Service is unavailable. Cannot add used part.", e);
        throw new ServiceUnavailableException("Service Request Service");
    }
}
