package com.vsms.servicerequest.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.feign.TechnicianFeignResponse;

//@FeignClient(name = "user-service")
@FeignClient(name = "user-service", url = "http://localhost:8081")

public interface UserFeignClient {

    @GetMapping("/internal/technicians/available")
    List<TechnicianFeignResponse> getAvailableTechnicians();

    @PutMapping("/internal/technicians/{technicianId}/status")
    void updateTechnicianStatus(
            @PathVariable String technicianId,
            @RequestParam boolean available
    );
}
