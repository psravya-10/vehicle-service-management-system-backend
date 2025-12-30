package com.vsms.user.service;

import com.vsms.user.dto.*;
import com.vsms.user.entity.User;
import com.vsms.user.enums.*;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;
import com.vsms.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public void registerCustomer(RegisterCustomerRequest req) {

        if (repo.findByEmail(req.getEmail()).isPresent())
            throw new BusinessException("Email already registered");

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .pincode(req.getPincode())
                .role(Role.CUSTOMER)
                .status(UserStatus.APPROVED) // customers auto approved
                .build();

        repo.save(user);
    }


    public void registerStaff(RegisterStaffRequest req) {

        if (req.getRole() == Role.CUSTOMER || req.getRole() == Role.ADMIN)
            throw new BusinessException("Invalid role for staff registration");

        if (repo.findByEmail(req.getEmail()).isPresent())
            throw new BusinessException("Email already registered");

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .pincode(req.getPincode())
                .role(req.getRole())
                .status(UserStatus.PENDING) // admin approval needed
                .availability(
                        req.getRole() == Role.TECHNICIAN
                                ? AvailabilityStatus.AVAILABLE
                                : null
                )
                .build();

        repo.save(user);
    }


    public LoginResponse login(LoginRequest req) {

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid credentials");

        if (user.getStatus() != UserStatus.APPROVED)
            throw new BusinessException("Account not approved by admin");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getRole());
    }
}
