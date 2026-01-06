package com.vsms.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.inventory.dto.CreateSparePartDto;
import com.vsms.inventory.entity.SparePart;
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
class PartControllerTest {

    @Mock
    private InventoryService service;

    @InjectMocks
    private PartController partController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SparePart testPart;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(partController).build();
        objectMapper = new ObjectMapper();

        testPart = SparePart.builder()
                .id("part-1")
                .name("Brake Pad")
                .category("Brakes")
                .availableQuantity(10)
                .unitPrice(50.0)
                .lowStockThreshold(3)
                .build();
    }

    @Test
    @DisplayName("POST /api/inventory/parts - Should add part successfully")
    void add_Success() throws Exception {
        CreateSparePartDto dto = new CreateSparePartDto();
        dto.setName("Oil Filter");
        dto.setCategory("Filters");
        dto.setAvailableQuantity(20);
        dto.setUnitPrice(15.0);

        when(service.addPart(any(CreateSparePartDto.class))).thenReturn("new-part-id");

        mockMvc.perform(post("/api/inventory/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partId").value("new-part-id"));

        verify(service).addPart(any(CreateSparePartDto.class));
    }

    @Test
    @DisplayName("GET /api/inventory/parts - Should return all parts")
    void all_ReturnsParts() throws Exception {
        when(service.getAllParts()).thenReturn(Arrays.asList(testPart));

        mockMvc.perform(get("/api/inventory/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("part-1"))
                .andExpect(jsonPath("$[0].name").value("Brake Pad"));

        verify(service).getAllParts();
    }

    @Test
    @DisplayName("GET /api/inventory/parts - Should return empty list")
    void all_EmptyList() throws Exception {
        when(service.getAllParts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/inventory/parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/inventory/parts/low-stock - Should return low stock parts")
    void lowStock_ReturnsLowStockParts() throws Exception {
        when(service.getLowStockParts()).thenReturn(Arrays.asList(testPart));

        mockMvc.perform(get("/api/inventory/parts/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).getLowStockParts();
    }

    @Test
    @DisplayName("PUT /api/inventory/parts/{id}/restock - Should restock part")
    void restock_Success() throws Exception {
        doNothing().when(service).restockPart("part-1", 5);

        mockMvc.perform(put("/api/inventory/parts/part-1/restock")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Part restocked successfully"));

        verify(service).restockPart("part-1", 5);
    }

    @Test
    @DisplayName("PUT /api/inventory/parts/{id}/threshold - Should update threshold")
    void updateThreshold_Success() throws Exception {
        doNothing().when(service).updateThreshold("part-1", 10);

        mockMvc.perform(put("/api/inventory/parts/part-1/threshold")
                        .param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Threshold updated successfully"));

        verify(service).updateThreshold("part-1", 10);
    }
}
