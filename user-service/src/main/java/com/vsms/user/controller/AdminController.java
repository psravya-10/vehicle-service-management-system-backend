package com.vsms.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.user.dto.ApproveUsersRequest;
import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/pending")
    public List<CustomerResponse> pendingUsers() {
        return adminService.pendingUsers();
    }

    @PutMapping("/approve")
    public void approve(@RequestBody ApproveUsersRequest request) {
        adminService.approveUsers(request.getIds());
    }


}
