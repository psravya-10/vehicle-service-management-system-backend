package com.vsms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.exception.GlobalExceptionHandler;
import com.vsms.user.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CustomerResponse technicianResponse;
    private CustomerResponse managerResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        technicianResponse = CustomerResponse.builder()
                .id("tech-1")
                .name("Test Technician")
                .email("tech@test.com")
                .role(Role.TECHNICIAN)
                .status(UserStatus.APPROVED)
                .phone("1234567890")
                .pincode("123456")
                .build();

        managerResponse = CustomerResponse.builder()
                .id("mgr-1")
                .name("Test Manager")
                .email("manager@test.com")
                .role(Role.MANAGER)
                .status(UserStatus.PENDING)
                .phone("9876543210")
                .pincode("654321")
                .build();
    }

    @Nested
    @DisplayName("GET /api/admin/staff")
    class GetAllStaffTests {

        @Test
        @DisplayName("Should return all staff without filters")
        void getAllStaff_NoFilters_ReturnsAll() throws Exception {
            List<CustomerResponse> staffList = Arrays.asList(technicianResponse, managerResponse);
            when(adminService.getAllStaff(null, null)).thenReturn(staffList);

            mockMvc.perform(get("/api/admin/staff"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("tech-1"))
                    .andExpect(jsonPath("$[1].id").value("mgr-1"));

            verify(adminService).getAllStaff(null, null);
        }

        @Test
        @DisplayName("Should return staff filtered by status")
        void getAllStaff_StatusFilter_ReturnsFiltered() throws Exception {
            when(adminService.getAllStaff(UserStatus.PENDING, null))
                    .thenReturn(Collections.singletonList(managerResponse));

            mockMvc.perform(get("/api/admin/staff")
                            .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));

            verify(adminService).getAllStaff(UserStatus.PENDING, null);
        }

        @Test
        @DisplayName("Should return staff filtered by role")
        void getAllStaff_RoleFilter_ReturnsFiltered() throws Exception {
            when(adminService.getAllStaff(null, Role.TECHNICIAN))
                    .thenReturn(Collections.singletonList(technicianResponse));

            mockMvc.perform(get("/api/admin/staff")
                            .param("role", "TECHNICIAN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].role").value("TECHNICIAN"));

            verify(adminService).getAllStaff(null, Role.TECHNICIAN);
        }

        @Test
        @DisplayName("Should return staff filtered by both status and role")
        void getAllStaff_BothFilters_ReturnsFiltered() throws Exception {
            when(adminService.getAllStaff(UserStatus.APPROVED, Role.TECHNICIAN))
                    .thenReturn(Collections.singletonList(technicianResponse));

            mockMvc.perform(get("/api/admin/staff")
                            .param("status", "APPROVED")
                            .param("role", "TECHNICIAN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].role").value("TECHNICIAN"))
                    .andExpect(jsonPath("$[0].status").value("APPROVED"));

            verify(adminService).getAllStaff(UserStatus.APPROVED, Role.TECHNICIAN);
        }

        @Test
        @DisplayName("Should return empty list when no staff found")
        void getAllStaff_NoStaff_ReturnsEmptyList() throws Exception {
            when(adminService.getAllStaff(null, null)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/staff"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void getAllUsers_ReturnsAll() throws Exception {
            List<CustomerResponse> usersList = Arrays.asList(technicianResponse, managerResponse);
            when(adminService.getAllUsers()).thenReturn(usersList);

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(adminService).getAllUsers();
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void getAllUsers_NoUsers_ReturnsEmptyList() throws Exception {
            when(adminService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/users/{id}/approval")
    class UpdateApprovalTests {

        @Test
        @DisplayName("Should approve user successfully")
        void updateApproval_Approve_Success() throws Exception {
            doNothing().when(adminService).updateApproval("tech-1", true);

            mockMvc.perform(put("/api/admin/users/tech-1/approval")
                            .param("approved", "true"))
                    .andExpect(status().isOk());

            verify(adminService).updateApproval("tech-1", true);
        }

        @Test
        @DisplayName("Should reject user successfully")
        void updateApproval_Reject_Success() throws Exception {
            doNothing().when(adminService).updateApproval("tech-1", false);

            mockMvc.perform(put("/api/admin/users/tech-1/approval")
                            .param("approved", "false"))
                    .andExpect(status().isOk());

            verify(adminService).updateApproval("tech-1", false);
        }

        @Test
        @DisplayName("Should return 400 when user not found")
        void updateApproval_UserNotFound_Returns400() throws Exception {
            doThrow(new BusinessException("User not found"))
                    .when(adminService).updateApproval("nonexistent", true);

            mockMvc.perform(put("/api/admin/users/nonexistent/approval")
                            .param("approved", "true"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }
}
