package com.vsms.inventory.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-request-service")
public interface ServiceRequestClient {

    @GetMapping("/api/service-requests/{id}")
    Object getServiceRequestById(@PathVariable String id);
}


