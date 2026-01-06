package com.vsms.user.service;

import com.vsms.user.dto.TechnicianResponse;
import com.vsms.user.entity.User;
import com.vsms.user.enums.AvailabilityStatus;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private ManagerService managerService;

    private User availableTech;
    private User busyTech;
    private User techWithNullAvailability;

    @BeforeEach
    void setUp() {
        availableTech = User.builder()
                .id("tech-1")
                .name("Available Tech")
                .email("available@test.com")
                .role(Role.TECHNICIAN)
                .status(UserStatus.APPROVED)
                .availability(AvailabilityStatus.AVAILABLE)
                .build();

        busyTech = User.builder()
                .id("tech-2")
                .name("Busy Tech")
                .email("busy@test.com")
                .role(Role.TECHNICIAN)
                .status(UserStatus.APPROVED)
                .availability(AvailabilityStatus.BUSY)
                .build();

        techWithNullAvailability = User.builder()
                .id("tech-3")
                .name("Null Availability Tech")
                .email("null@test.com")
                .role(Role.TECHNICIAN)
                .status(UserStatus.APPROVED)
                .availability(null)
                .build();
    }

    @Nested
    @DisplayName("getAllTechnicians Tests")
    class GetAllTechniciansTests {

        @Test
        @DisplayName("Should return all approved technicians")
        void getAllTechnicians_ReturnsTechnicians() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Arrays.asList(availableTech, busyTech));

            List<TechnicianResponse> result = managerService.getAllTechnicians();

            assertEquals(2, result.size());
            verify(repo).findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED);
        }

        @Test
        @DisplayName("Should return empty list when no technicians")
        void getAllTechnicians_NoTechnicians_ReturnsEmptyList() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Collections.emptyList());

            List<TechnicianResponse> result = managerService.getAllTechnicians();

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should map technician fields correctly")
        void getAllTechnicians_CorrectMapping() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Collections.singletonList(availableTech));

            List<TechnicianResponse> result = managerService.getAllTechnicians();

            assertEquals(1, result.size());
            TechnicianResponse response = result.get(0);
            assertEquals("tech-1", response.getId());
            assertEquals("Available Tech", response.getName());
            assertEquals("available@test.com", response.getEmail());
            assertEquals(AvailabilityStatus.AVAILABLE, response.getAvailability());
        }
    }

    @Nested
    @DisplayName("getAvailableTechnicians Tests")
    class GetAvailableTechniciansTests {

        @Test
        @DisplayName("Should return only available technicians")
        void getAvailableTechnicians_ReturnsAvailableOnly() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Arrays.asList(availableTech, busyTech, techWithNullAvailability));

            List<TechnicianResponse> result = managerService.getAvailableTechnicians();

            assertEquals(1, result.size());
            assertEquals("tech-1", result.get(0).getId());
            assertEquals(AvailabilityStatus.AVAILABLE, result.get(0).getAvailability());
        }

        @Test
        @DisplayName("Should return empty list when no available technicians")
        void getAvailableTechnicians_NoneAvailable_ReturnsEmptyList() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Collections.singletonList(busyTech));

            List<TechnicianResponse> result = managerService.getAvailableTechnicians();

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should filter out technicians with null availability")
        void getAvailableTechnicians_FiltersNullAvailability() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Collections.singletonList(techWithNullAvailability));

            List<TechnicianResponse> result = managerService.getAvailableTechnicians();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateTechnicianAvailability Tests")
    class UpdateTechnicianAvailabilityTests {

        @Test
        @DisplayName("Should update availability to BUSY successfully")
        void updateTechnicianAvailability_ToBusy_Success() {
            when(repo.findById("tech-1")).thenReturn(Optional.of(availableTech));

            managerService.updateTechnicianAvailability("tech-1", AvailabilityStatus.BUSY);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(repo).save(userCaptor.capture());
            assertEquals(AvailabilityStatus.BUSY, userCaptor.getValue().getAvailability());
        }

        @Test
        @DisplayName("Should update availability to AVAILABLE successfully")
        void updateTechnicianAvailability_ToAvailable_Success() {
            when(repo.findById("tech-2")).thenReturn(Optional.of(busyTech));

            managerService.updateTechnicianAvailability("tech-2", AvailabilityStatus.AVAILABLE);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(repo).save(userCaptor.capture());
            assertEquals(AvailabilityStatus.AVAILABLE, userCaptor.getValue().getAvailability());
        }

        @Test
        @DisplayName("Should throw exception when technician not found")
        void updateTechnicianAvailability_NotFound_ThrowsException() {
            when(repo.findById("nonexistent")).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> managerService.updateTechnicianAvailability("nonexistent", AvailabilityStatus.BUSY));

            assertEquals("Technician not found", exception.getMessage());
            verify(repo, never()).save(any(User.class));
        }

    }
}
