package com.vsms.servicerequest.controller;

import com.vsms.servicerequest.dto.TechnicianUpdateServiceStatusDto;
import com.vsms.servicerequest.entity.ServicePriority;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;
import com.vsms.servicerequest.service.InventoryServiceClient;
import com.vsms.servicerequest.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceRequestControllerTest {

    @Mock
    private ServiceRequestService service;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private TechnicianServiceRequestController controller;

    private ServiceRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = ServiceRequest.builder()
                .id("sr-1")
                .userId("user-1")
                .vehicleId("vehicle-1")
                .issueDescription("Test issue")
                .technicianId("tech-1")
                .priority(ServicePriority.NORMAL)
                .status(ServiceStatus.ASSIGNED)
                .build();
    }

    @Test
    void getMyAssignedTasks_ReturnsTasks() {
        when(service.getByTechnicianId("tech-1")).thenReturn(Arrays.asList(testRequest));

        ResponseEntity<List<ServiceRequest>> response = controller.getMyAssignedTasks("tech-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getMyAssignedTasks_EmptyList() {
        when(service.getByTechnicianId("tech-2")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ServiceRequest>> response = controller.getMyAssignedTasks("tech-2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }


    @Test
    void updateStatus_ToInProgress_Success() {
        TechnicianUpdateServiceStatusDto dto = new TechnicianUpdateServiceStatusDto();
        dto.setStatus(ServiceStatus.IN_PROGRESS);
        dto.setRemarks("Started working");

        when(service.getById("sr-1")).thenReturn(testRequest);
        when(service.save(any(ServiceRequest.class))).thenReturn(testRequest);

        controller.updateStatus("sr-1", dto);

        verify(service).save(any(ServiceRequest.class));
    }

    @Test
    void updateStatus_ToCompleted_Success() {
        TechnicianUpdateServiceStatusDto dto = new TechnicianUpdateServiceStatusDto();
        dto.setStatus(ServiceStatus.COMPLETED);
        dto.setRemarks("Completed work");

        when(service.getById("sr-1")).thenReturn(testRequest);
        when(inventoryServiceClient.hasPendingRequests("sr-1")).thenReturn(false);
        when(service.save(any(ServiceRequest.class))).thenReturn(testRequest);

        controller.updateStatus("sr-1", dto);

        verify(inventoryServiceClient).hasPendingRequests("sr-1");
        verify(service).save(any(ServiceRequest.class));
    }

    @Test
    void updateStatus_ToCompleted_WithPendingParts_ThrowsException() {
        TechnicianUpdateServiceStatusDto dto = new TechnicianUpdateServiceStatusDto();
        dto.setStatus(ServiceStatus.COMPLETED);
        dto.setRemarks("Completed work");

        when(service.getById("sr-1")).thenReturn(testRequest);
        when(inventoryServiceClient.hasPendingRequests("sr-1")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> controller.updateStatus("sr-1", dto));
    }

    @Test
    void updateStatus_InvalidStatus_ThrowsException() {
        TechnicianUpdateServiceStatusDto dto = new TechnicianUpdateServiceStatusDto();
        dto.setStatus(ServiceStatus.CLOSED);
        dto.setRemarks("Trying to close");

        when(service.getById("sr-1")).thenReturn(testRequest);

        assertThrows(RuntimeException.class, () -> controller.updateStatus("sr-1", dto));
    }

    @Test
    void updateStatus_ToRequested_ThrowsException() {
        TechnicianUpdateServiceStatusDto dto = new TechnicianUpdateServiceStatusDto();
        dto.setStatus(ServiceStatus.REQUESTED);
        dto.setRemarks("Invalid status");

        when(service.getById("sr-1")).thenReturn(testRequest);

        assertThrows(RuntimeException.class, () -> controller.updateStatus("sr-1", dto));
    }


}
