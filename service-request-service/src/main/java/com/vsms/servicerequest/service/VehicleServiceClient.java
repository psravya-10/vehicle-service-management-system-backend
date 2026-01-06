package com.vsms.servicerequest.service;

import org.springframework.stereotype.Service;

import com.vsms.servicerequest.dto.feign.VehicleFeignResponse;
import com.vsms.servicerequest.exception.ServiceUnavailableException;
import com.vsms.servicerequest.feign.VehicleFeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceClient {

    private final VehicleFeignClient vehicleFeignClient;

    @CircuitBreaker(name = "vehicleService", fallbackMethod = "getVehicleByIdFallback")
    public VehicleFeignResponse getVehicleById(String id) {
        return vehicleFeignClient.getVehicleById(id);
    }

    public VehicleFeignResponse getVehicleByIdFallback(String id, Exception e) {
        log.error("Vehicle Service is unavailable. Cannot get vehicle details.", e);
        throw new ServiceUnavailableException("Vehicle Service");
    }
}
