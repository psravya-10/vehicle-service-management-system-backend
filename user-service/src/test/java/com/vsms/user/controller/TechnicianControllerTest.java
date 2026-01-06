package com.vsms.user.controller;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.enums.Role;
import com.vsms.user.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private TechnicianController technicianController;

    private CustomerResponse technicianResponse;

    @BeforeEach
    void setUp() {
        technicianResponse = CustomerResponse.builder()
                .id("tech-1")
                .name("Test Technician")
                .email("tech@test.com")
                .role(Role.TECHNICIAN)
                .build();
    }

    @Test
    @DisplayName("GET /api/technician/me - Should return technician profile")
    void myProfile_Success() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("tech@test.com");
        when(customerService.getByEmail("tech@test.com")).thenReturn(technicianResponse);

        CustomerResponse result = technicianController.myProfile(mockAuth);

        assertNotNull(result);
        assertEquals("tech-1", result.getId());
        assertEquals("Test Technician", result.getName());
        assertEquals("tech@test.com", result.getEmail());
        assertEquals(Role.TECHNICIAN, result.getRole());
        verify(customerService).getByEmail("tech@test.com");
    }
}
