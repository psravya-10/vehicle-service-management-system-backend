package com.vsms.user.controller;

import com.vsms.user.dto.TechnicianResponse;
import com.vsms.user.enums.AvailabilityStatus;
import com.vsms.user.exception.GlobalExceptionHandler;
import com.vsms.user.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock
    private ManagerService managerService;

    @InjectMocks
    private ManagerController managerController;

    private MockMvc mockMvc;

    private TechnicianResponse availableTech;
    private TechnicianResponse busyTech;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        availableTech = TechnicianResponse.builder()
                .id("tech-1")
                .name("Available Tech")
                .email("available@test.com")
                .availability(AvailabilityStatus.AVAILABLE)
                .build();

        busyTech = TechnicianResponse.builder()
                .id("tech-2")
                .name("Busy Tech")
                .email("busy@test.com")
                .availability(AvailabilityStatus.BUSY)
                .build();
    }

    @Test
    @DisplayName("GET /api/manager/technicians - Should return all technicians")
    void allTechnicians_ReturnsAll() throws Exception {
        List<TechnicianResponse> technicians = Arrays.asList(availableTech, busyTech);
        when(managerService.getAllTechnicians()).thenReturn(technicians);

        mockMvc.perform(get("/api/manager/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("tech-1"))
                .andExpect(jsonPath("$[1].id").value("tech-2"));

        verify(managerService).getAllTechnicians();
    }

    @Test
    @DisplayName("GET /api/manager/technicians - Should return empty list when no technicians")
    void allTechnicians_NoTechnicians_ReturnsEmptyList() throws Exception {
        when(managerService.getAllTechnicians()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/manager/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/manager/technicians/available - Should return available technicians")
    void availableTechnicians_ReturnsAvailable() throws Exception {
        when(managerService.getAvailableTechnicians())
                .thenReturn(Collections.singletonList(availableTech));

        mockMvc.perform(get("/api/manager/technicians/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("tech-1"))
                .andExpect(jsonPath("$[0].availability").value("AVAILABLE"));

        verify(managerService).getAvailableTechnicians();
    }

    @Test
    @DisplayName("GET /api/manager/technicians/available - Should return empty list when none available")
    void availableTechnicians_NoneAvailable_ReturnsEmptyList() throws Exception {
        when(managerService.getAvailableTechnicians()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/manager/technicians/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
