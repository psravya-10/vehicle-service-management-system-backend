package com.vsms.user.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.service.CustomerService;

import lombok.RequiredArgsConstructor;

/**
 * Internal API controller for inter-service communication.
 * These endpoints are called by other microservices (like service-request-service).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final CustomerService customerService;

    /**
     * Get user email by user ID.
     * Called by service-request-service for sending notifications.
     */
    @GetMapping("/{userId}/email")
    public String getUserEmail(@PathVariable String userId) {
        return customerService.getEmailByUserId(userId);
    }

    /**
     * Get user ID by email.
     * Called by frontend to get user's MongoDB ID.
     */
    @GetMapping("/email/{email}/id")
    public String getUserIdByEmail(@PathVariable String email) {
        CustomerResponse customer = customerService.getByEmail(email);
        return customer.getId();
    }
}
