package com.vsms.user.controller;

import com.vsms.user.dto.*;
import com.vsms.user.service.AuthService;
import com.vsms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;

    @PostMapping("/register/customer")
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest req) {

        String customerId = authService.registerCustomer(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterCustomerResponse(customerId));
    }


    @PostMapping("/register/staff")
    public ResponseEntity<Void> registerStaff(@Valid @RequestBody RegisterStaffRequest req) {

        authService.registerStaff(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/profile")
    public CustomerResponse getMyProfile(Authentication auth) {
        return customerService.getByEmail(auth.getName());
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(Authentication auth, @Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(auth.getName(), req);
        return ResponseEntity.ok().build();
    }
}
