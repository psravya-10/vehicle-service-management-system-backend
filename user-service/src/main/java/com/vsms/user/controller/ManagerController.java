package com.vsms.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.user.dto.TechnicianResponse;
import com.vsms.user.service.ManagerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/technicians")
    public List<TechnicianResponse> allTechnicians() {
        return managerService.getAllTechnicians();
    }

    @GetMapping("/technicians/available")
    public List<TechnicianResponse> availableTechnicians() {
        return managerService.getAvailableTechnicians(); 
    }


}

