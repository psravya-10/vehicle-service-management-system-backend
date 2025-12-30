package com.vsms.user.controller;

import com.vsms.user.dto.*;
import com.vsms.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public void registerCustomer(@Valid @RequestBody RegisterCustomerRequest req) {
        authService.registerCustomer(req);
    }

    @PostMapping("/register/staff")
    public void registerStaff(@Valid @RequestBody RegisterStaffRequest req) {
        authService.registerStaff(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
