package com.vsms.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vsms.user.dto.TechnicianResponse;
import com.vsms.user.entity.User;
import com.vsms.user.enums.AvailabilityStatus;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository repo;

    public List<TechnicianResponse> getAllTechnicians() {
        return repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<TechnicianResponse> getAvailableTechnicians() {
        return repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED)
                .stream()
                .filter(t -> t.getAvailability() != null)
                .filter(t -> AvailabilityStatus.AVAILABLE.equals(t.getAvailability()))
                .map(this::map)
                .toList();
    }

    public void updateTechnicianAvailability(String technicianId, AvailabilityStatus status) {

        User technician = repo.findById(technicianId)
                .orElseThrow(() -> new BusinessException("Technician not found"));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new BusinessException("User is not a technician");
        }

        technician.setAvailability(status);
        repo.save(technician);
    }
    private TechnicianResponse map(User u) {
        return TechnicianResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .availability(u.getAvailability())
                .build();
    }
}
