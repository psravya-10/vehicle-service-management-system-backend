package com.vsms.servicerequest.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {

    @GetMapping("/api/inventory/requests/pending")
    boolean hasPendingRequests(@RequestParam String serviceRequestId);
}
