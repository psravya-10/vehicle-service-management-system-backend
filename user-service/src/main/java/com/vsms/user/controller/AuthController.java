package com.vsms.user.controller;

import com.vsms.user.dto.*;
import com.vsms.user.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<Void> registerCustomer( @Valid @RequestBody RegisterCustomerRequest req) {

        authService.registerCustomer(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
}
