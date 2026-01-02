package com.vsms.inventory.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vsms.inventory.dto.UsedPartDto;

@FeignClient(name = "service-request-service" )
public interface ServiceRequestClient {

    @GetMapping("/api/service-requests/{id}")
    Object getServiceRequestById(@PathVariable String id);
    
    @PostMapping("/internal/service-requests/{id}/parts")
    void addUsedPart(
        @PathVariable String id,
        @RequestBody UsedPartDto dto
    );
}


