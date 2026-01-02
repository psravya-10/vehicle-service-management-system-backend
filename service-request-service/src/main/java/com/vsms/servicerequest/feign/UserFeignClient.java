package com.vsms.servicerequest.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.vsms.servicerequest.dto.feign.TechnicianFeignResponse;

@FeignClient(name = "user-service")

public interface UserFeignClient {

    @GetMapping("/api/manager/technicians/available")
    List<TechnicianFeignResponse> getAvailableTechnicians();

    @PutMapping("/internal/technicians/{technicianId}/status")
    void updateTechnicianStatus(
            @PathVariable String technicianId,
            @RequestParam boolean available
    );
}
