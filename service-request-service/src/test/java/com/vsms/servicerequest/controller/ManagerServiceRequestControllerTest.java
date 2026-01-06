package com.vsms.servicerequest.controller;

import com.vsms.servicerequest.dto.AssignServiceRequestDto;
import com.vsms.servicerequest.entity.ServicePriority;
import com.vsms.servicerequest.entity.ServiceRequest;
import com.vsms.servicerequest.entity.ServiceStatus;
import com.vsms.servicerequest.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceRequestControllerTest {

    @Mock
    private ServiceRequestService service;

    @InjectMocks
    private ManagerServiceRequestController controller;

    private ServiceRequest testRequest;
    private AssignServiceRequestDto assignDto;

    @BeforeEach
    void setUp() {
        testRequest = ServiceRequest.builder()
                .id("sr-1")
                .userId("user-1")
                .vehicleId("vehicle-1")
                .issueDescription("Test issue")
                .priority(ServicePriority.NORMAL)
                .status(ServiceStatus.REQUESTED)
                .build();

        assignDto = new AssignServiceRequestDto();
        assignDto.setTechnicianId("tech-1");
        assignDto.setBayId("bay-1");
    }

    // ========== all() tests ==========

    @Test
    void all_ReturnsAllRequests() {
        when(service.getAll()).thenReturn(Arrays.asList(testRequest));

        List<ServiceRequest> result = controller.all();

        assertEquals(1, result.size());
        verify(service).getAll();
    }

    @Test
    void all_EmptyList() {
        when(service.getAll()).thenReturn(Collections.emptyList());

        List<ServiceRequest> result = controller.all();

        assertTrue(result.isEmpty());
    }

    // ========== pending() tests ==========

    @Test
    void pending_ReturnsPendingRequests() {
        when(service.getPending()).thenReturn(Arrays.asList(testRequest));

        List<ServiceRequest> result = controller.pending();

        assertEquals(1, result.size());
        verify(service).getPending();
    }

    @Test
    void pending_EmptyList() {
        when(service.getPending()).thenReturn(Collections.emptyList());

        List<ServiceRequest> result = controller.pending();

        assertTrue(result.isEmpty());
    }

    // ========== getById() tests ==========

    @Test
    void getById_ReturnsRequest() {
        when(service.getById("sr-1")).thenReturn(testRequest);

        ServiceRequest result = controller.getById("sr-1");

        assertEquals("sr-1", result.getId());
    }

    // ========== assign() tests ==========

    @Test
    void assign_Success() {
        doNothing().when(service).assign("sr-1", assignDto);

        controller.assign("sr-1", assignDto);

        verify(service).assign("sr-1", assignDto);
    }

    // ========== close() tests ==========

    @Test
    void close_Success() {
        doNothing().when(service).closeService("sr-1", 100.0);

        controller.close("sr-1", 100.0);

        verify(service).closeService("sr-1", 100.0);
    }

    @Test
    void close_WithDifferentLabourCharges() {
        doNothing().when(service).closeService("sr-1", 250.0);

        controller.close("sr-1", 250.0);

        verify(service).closeService("sr-1", 250.0);
    }

    // ========== getTechnicianWorkload() tests ==========

    @Test
    void getTechnicianWorkload_ReturnsWorkload() {
        testRequest.setTechnicianId("tech-1");
        when(service.getAll()).thenReturn(Arrays.asList(testRequest));
        when(service.getTechnicianWorkload("tech-1")).thenReturn(2L);

        Map<String, Long> result = controller.getTechnicianWorkload();

        assertEquals(1, result.size());
        assertEquals(2L, result.get("tech-1"));
    }

    @Test
    void getTechnicianWorkload_EmptyWhenNoTechnicians() {
        when(service.getAll()).thenReturn(Collections.emptyList());

        Map<String, Long> result = controller.getTechnicianWorkload();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTechnicianWorkload_IgnoresNullTechnicianId() {
        testRequest.setTechnicianId(null);
        when(service.getAll()).thenReturn(Arrays.asList(testRequest));

        Map<String, Long> result = controller.getTechnicianWorkload();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTechnicianWorkload_MultipleTechnicians() {
        ServiceRequest request1 = ServiceRequest.builder()
                .id("sr-1")
                .technicianId("tech-1")
                .build();
        ServiceRequest request2 = ServiceRequest.builder()
                .id("sr-2")
                .technicianId("tech-2")
                .build();
        when(service.getAll()).thenReturn(Arrays.asList(request1, request2));
        when(service.getTechnicianWorkload("tech-1")).thenReturn(1L);
        when(service.getTechnicianWorkload("tech-2")).thenReturn(3L);

        Map<String, Long> result = controller.getTechnicianWorkload();

        assertEquals(2, result.size());
        assertEquals(1L, result.get("tech-1"));
        assertEquals(3L, result.get("tech-2"));
    }
}
