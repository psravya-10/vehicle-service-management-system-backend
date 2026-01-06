package com.vsms.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.inventory.dto.CreatePartRequestDto;
import com.vsms.inventory.entity.PartRequest;
import com.vsms.inventory.entity.RequestStatus;
import com.vsms.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PartRequestControllerTest {

    @Mock
    private InventoryService service;

    @InjectMocks
    private PartRequestController partRequestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PartRequest testRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(partRequestController).build();
        objectMapper = new ObjectMapper();

        testRequest = PartRequest.builder()
                .id("request-1")
                .serviceRequestId("sr-1")
                .technicianId("tech-1")
                .partId("part-1")
                .quantity(2)
                .status(RequestStatus.REQUESTED)
                .build();
    }

    @Test
    @DisplayName("GET /api/inventory/requests - Should return all requests")
    void all_NoFilter_ReturnsAll() throws Exception {
        when(service.getAllPartRequests()).thenReturn(Arrays.asList(testRequest));

        mockMvc.perform(get("/api/inventory/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("request-1"));

        verify(service).getAllPartRequests();
    }

    @Test
    @DisplayName("GET /api/inventory/requests?status=REQUESTED - Should return filtered requests")
    void all_WithStatusFilter_ReturnsFiltered() throws Exception {
        when(service.getPartRequestsByStatus(RequestStatus.REQUESTED))
                .thenReturn(Arrays.asList(testRequest));

        mockMvc.perform(get("/api/inventory/requests")
                        .param("status", "REQUESTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).getPartRequestsByStatus(RequestStatus.REQUESTED);
    }

    @Test
    @DisplayName("POST /api/inventory/requests - Should create part request")
    void requestPart_Success() throws Exception {
        CreatePartRequestDto dto = new CreatePartRequestDto();
        dto.setServiceRequestId("sr-1");
        dto.setTechnicianId("tech-1");
        dto.setPartId("part-1");
        dto.setQuantity(2);

        when(service.requestPart(any(CreatePartRequestDto.class))).thenReturn("new-request-id");

        mockMvc.perform(post("/api/inventory/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value("new-request-id"));

        verify(service).requestPart(any(CreatePartRequestDto.class));
    }

    @Test
    @DisplayName("PUT /api/inventory/requests/{id}/approve - Should approve request")
    void approve_Success() throws Exception {
        doNothing().when(service).approveRequest("request-1", "manager-1");

        mockMvc.perform(put("/api/inventory/requests/request-1/approve")
                        .param("managerId", "manager-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Part request approved successfully"));

        verify(service).approveRequest("request-1", "manager-1");
    }

    @Test
    @DisplayName("GET /api/inventory/requests/pending - Should return true when pending")
    void hasPendingRequests_HasPending_ReturnsTrue() throws Exception {
        when(service.hasPendingRequests("sr-1")).thenReturn(true);

        mockMvc.perform(get("/api/inventory/requests/pending")
                        .param("serviceRequestId", "sr-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(service).hasPendingRequests("sr-1");
    }

    @Test
    @DisplayName("GET /api/inventory/requests/pending - Should return false when no pending")
    void hasPendingRequests_NoPending_ReturnsFalse() throws Exception {
        when(service.hasPendingRequests("sr-1")).thenReturn(false);

        mockMvc.perform(get("/api/inventory/requests/pending")
                        .param("serviceRequestId", "sr-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
