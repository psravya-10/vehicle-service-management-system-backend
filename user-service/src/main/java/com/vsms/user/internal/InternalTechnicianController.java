package com.vsms.user.internal;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vsms.user.dto.TechnicianResponse;
import com.vsms.user.enums.AvailabilityStatus;
import com.vsms.user.service.ManagerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/technicians")
@RequiredArgsConstructor
public class InternalTechnicianController {

    private final ManagerService managerService;

    // Used by Service Request Service
    @GetMapping
    public List<TechnicianResponse> allTechnicians() {
        return managerService.getAllTechnicians();
    }

    @GetMapping("/available")
    public List<TechnicianResponse> availableTechnicians() {
        return managerService.getAvailableTechnicians();
    }

    @PutMapping("/{technicianId}/status")
    public void updateTechnicianStatus(
            @PathVariable String technicianId,
            @RequestParam boolean available) {

        AvailabilityStatus status = available
                ? AvailabilityStatus.AVAILABLE
                : AvailabilityStatus.BUSY;

        managerService.updateTechnicianAvailability(technicianId, status);
    }

}
