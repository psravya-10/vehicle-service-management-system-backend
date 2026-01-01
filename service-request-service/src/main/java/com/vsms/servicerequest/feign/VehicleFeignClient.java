package com.vsms.servicerequest.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.feign.VehicleFeignResponse;

@FeignClient(name = "vehicle-service")
public interface VehicleFeignClient {

    @GetMapping("/api/vehicles/{id}")
    VehicleFeignResponse getVehicleById(@PathVariable String id);
}
