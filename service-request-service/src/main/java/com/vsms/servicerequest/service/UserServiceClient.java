package com.vsms.servicerequest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vsms.servicerequest.dto.feign.TechnicianFeignResponse;
import com.vsms.servicerequest.exception.ServiceUnavailableException;
import com.vsms.servicerequest.feign.UserFeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final UserFeignClient userFeignClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "getAvailableTechniciansFallback")
    public List<TechnicianFeignResponse> getAvailableTechnicians() {
        return userFeignClient.getAvailableTechnicians();
    }

    public List<TechnicianFeignResponse> getAvailableTechniciansFallback(Exception e) {
        log.error("User Service is unavailable. Circuit breaker activated.", e);
        throw new ServiceUnavailableException("User Service");
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "updateTechnicianStatusFallback")
    public void updateTechnicianStatus(String technicianId, boolean available) {
        userFeignClient.updateTechnicianStatus(technicianId, available);
    }

    public void updateTechnicianStatusFallback(String technicianId, boolean available, Exception e) {
        log.error("User Service is unavailable. Cannot update technician status.", e);
        throw new ServiceUnavailableException("User Service");
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserEmailFallback")
    public String getUserEmail(String userId) {
        return userFeignClient.getUserEmail(userId);
    }

    public String getUserEmailFallback(String userId, Exception e) {
        log.error("User Service is unavailable. Cannot get user email.", e);
        throw new ServiceUnavailableException("User Service");
    }
}
