package com.vsms.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.dto.NotificationEvent;
import com.vsms.user.entity.User;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.messaging.NotificationPublisher;
import com.vsms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repo;
    private final NotificationPublisher notificationPublisher;

    public List<CustomerResponse> getAllStaff(UserStatus status, Role role) {
        List<Role> staffRoles = List.of(Role.TECHNICIAN, Role.MANAGER);
        
        List<User> users;
        
        if (role != null && status != null) {
            users = repo.findByRoleAndStatus(role, status);
        } else if (role != null) {
            users = repo.findByRole(role);
        } else if (status != null) {
            users = repo.findByRoleInAndStatus(staffRoles, status);
        } else {
            users = repo.findByRoleIn(staffRoles);
        }
        
        return users.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<CustomerResponse> getAllUsers() {
        return repo.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void updateApproval(String userId, boolean approved) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setStatus(approved ? UserStatus.APPROVED : UserStatus.REJECTED);
        repo.save(user);

        String message = approved 
            ? "Your registration as " + user.getRole() + " has been approved. You can now login."
            : "Your registration as " + user.getRole() + " has been rejected.";

        notificationPublisher.publish(
            NotificationEvent.builder()
                .eventType(approved ? "REGISTRATION_APPROVED" : "REGISTRATION_REJECTED")
                .userId(user.getId())
                .userEmail(user.getEmail())
                .message(message)
                .build()
        );
    }

    private CustomerResponse mapToResponse(User u) {
        return CustomerResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .status(u.getStatus())
                .phone(u.getPhone())
                .pincode(u.getPincode())
                .build();
    }
}
