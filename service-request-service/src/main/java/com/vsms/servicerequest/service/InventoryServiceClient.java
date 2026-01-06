package com.vsms.servicerequest.service;

import org.springframework.stereotype.Service;

import com.vsms.servicerequest.exception.ServiceUnavailableException;
import com.vsms.servicerequest.feign.InventoryFeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceClient {

    private final InventoryFeignClient inventoryFeignClient;

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "hasPendingRequestsFallback")
    public boolean hasPendingRequests(String serviceRequestId) {
        return inventoryFeignClient.hasPendingRequests(serviceRequestId);
    }

    public boolean hasPendingRequestsFallback(String serviceRequestId, Exception e) {
        log.error("Inventory Service is unavailable. Cannot check pending requests.", e);
        throw new ServiceUnavailableException("Inventory Service");
    }
}
