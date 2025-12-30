package com.vsms.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.service.CustomerService;
import com.vsms.user.service.TechnicianService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/technician")
@RequiredArgsConstructor
public class TechnicianController {

    private final CustomerService customerService;

    @GetMapping("/me")
    public CustomerResponse myProfile(Authentication auth) {
        return customerService.getByEmail(auth.getName());
    }
}



