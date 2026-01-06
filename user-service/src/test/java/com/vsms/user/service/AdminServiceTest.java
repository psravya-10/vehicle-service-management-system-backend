package com.vsms.user.service;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.dto.NotificationEvent;
import com.vsms.user.entity.User;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.messaging.NotificationPublisher;
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
class AdminServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private AdminService adminService;

    private User technician1;
    private User technician2;
    private User manager1;
    private User customer1;
    private User pendingTechnician;

    @BeforeEach
    void setUp() {
        technician1 = User.builder()
                .id("tech-1")
                .name("Technician One")
                .email("tech1@test.com")
                .phone("1111111111")
                .pincode("111111")
                .role(Role.TECHNICIAN)
                .status(UserStatus.APPROVED)
                .build();

        technician2 = User.builder()
                .id("tech-2")
                .name("Technician Two")
                .email("tech2@test.com")
                .phone("2222222222")
                .pincode("222222")
                .role(Role.TECHNICIAN)
                .status(UserStatus.PENDING)
                .build();

        manager1 = User.builder()
                .id("mgr-1")
                .name("Manager One")
                .email("manager1@test.com")
                .phone("3333333333")
                .pincode("333333")
                .role(Role.MANAGER)
                .status(UserStatus.APPROVED)
                .build();

        customer1 = User.builder()
                .id("cust-1")
                .name("Customer One")
                .email("customer1@test.com")
                .phone("4444444444")
                .pincode("444444")
                .role(Role.CUSTOMER)
                .status(UserStatus.APPROVED)
                .build();

        pendingTechnician = User.builder()
                .id("pending-tech-1")
                .name("Pending Tech")
                .email("pendingtech@test.com")
                .phone("5555555555")
                .pincode("555555")
                .role(Role.TECHNICIAN)
                .status(UserStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("getAllStaff Tests")
    class GetAllStaffTests {

        @Test
        @DisplayName("Should return all staff when no filters")
        void getAllStaff_NoFilters_ReturnsAllStaff() {
            List<Role> staffRoles = List.of(Role.TECHNICIAN, Role.MANAGER);
            when(repo.findByRoleIn(staffRoles)).thenReturn(Arrays.asList(technician1, technician2, manager1));

            List<CustomerResponse> result = adminService.getAllStaff(null, null);

            assertEquals(3, result.size());
            verify(repo).findByRoleIn(staffRoles);
        }
        @Test
        @DisplayName("Should return staff filtered by role and status")
        void getAllStaff_RoleAndStatusFilter_ReturnsFilteredStaff() {
            when(repo.findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED))
                    .thenReturn(Collections.singletonList(technician1));

            List<CustomerResponse> result = adminService.getAllStaff(UserStatus.APPROVED, Role.TECHNICIAN);

            assertEquals(1, result.size());
            assertEquals("tech-1", result.get(0).getId());
            assertEquals(Role.TECHNICIAN, result.get(0).getRole());
            assertEquals(UserStatus.APPROVED, result.get(0).getStatus());
            verify(repo).findByRoleAndStatus(Role.TECHNICIAN, UserStatus.APPROVED);
        }

        @Test
        @DisplayName("Should return empty list when no staff found")
        void getAllStaff_NoStaffFound_ReturnsEmptyList() {
            List<Role> staffRoles = List.of(Role.TECHNICIAN, Role.MANAGER);
            when(repo.findByRoleIn(staffRoles)).thenReturn(Collections.emptyList());

            List<CustomerResponse> result = adminService.getAllStaff(null, null);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void getAllUsers_ReturnsAllUsers() {
            when(repo.findAll()).thenReturn(Arrays.asList(technician1, manager1, customer1));

            List<CustomerResponse> result = adminService.getAllUsers();

            assertEquals(3, result.size());
            verify(repo).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void getAllUsers_NoUsers_ReturnsEmptyList() {
            when(repo.findAll()).thenReturn(Collections.emptyList());

            List<CustomerResponse> result = adminService.getAllUsers();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateApproval Tests")
    class UpdateApprovalTests {

        @Test
        @DisplayName("Should approve user successfully")
        void updateApproval_Approve_Success() {
            when(repo.findById("pending-tech-1")).thenReturn(Optional.of(pendingTechnician));

            adminService.updateApproval("pending-tech-1", true);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(repo).save(userCaptor.capture());
            assertEquals(UserStatus.APPROVED, userCaptor.getValue().getStatus());

            ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
            verify(notificationPublisher).publish(eventCaptor.capture());
            assertEquals("REGISTRATION_APPROVED", eventCaptor.getValue().getEventType());
            assertEquals("pendingtech@test.com", eventCaptor.getValue().getUserEmail());
            assertTrue(eventCaptor.getValue().getMessage().contains("approved"));
        }

        @Test
        @DisplayName("Should reject user successfully")
        void updateApproval_Reject_Success() {
            when(repo.findById("pending-tech-1")).thenReturn(Optional.of(pendingTechnician));

            adminService.updateApproval("pending-tech-1", false);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(repo).save(userCaptor.capture());
            assertEquals(UserStatus.REJECTED, userCaptor.getValue().getStatus());

            ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
            verify(notificationPublisher).publish(eventCaptor.capture());
            assertEquals("REGISTRATION_REJECTED", eventCaptor.getValue().getEventType());
            assertTrue(eventCaptor.getValue().getMessage().contains("rejected"));
        }

    }

}
