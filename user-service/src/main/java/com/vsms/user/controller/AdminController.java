package com.vsms.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/staff")
    public List<CustomerResponse> getAllStaff(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Role role) {
        return adminService.getAllStaff(status, role);
    }

    @GetMapping("/users")
    public List<CustomerResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    // Approve or reject a user
    @PutMapping("/users/{id}/approval")
    public String updateApproval(
            @PathVariable String id,
            @RequestParam boolean approved) {
        adminService.updateApproval(id, approved);
        return approved ? "Staff approved successfully" : "Staff rejected successfully";
    }
}
