package com.vsms.servicerequest.controller;

import com.vsms.servicerequest.dto.CreateServiceRequestDto;
import com.vsms.servicerequest.dto.CreateServiceRequestResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceRequestControllerTest {

    @Mock
    private ServiceRequestService service;

    @InjectMocks
    private CustomerServiceRequestController controller;

    private ServiceRequest testRequest;
    private CreateServiceRequestDto createDto;

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

        createDto = new CreateServiceRequestDto();
        createDto.setUserId("user-1");
        createDto.setVehicleId("vehicle-1");
        createDto.setIssueDescription("Test issue");
        createDto.setPriority(ServicePriority.NORMAL);
    }

    // ========== create() tests ==========

    @Test
    void create_Success() {
        when(service.create(any(CreateServiceRequestDto.class))).thenReturn(testRequest);

        ResponseEntity<CreateServiceRequestResponse> response = controller.create(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("sr-1", response.getBody().getId());
    }

    // ========== getById() tests ==========

    @Test
    void getById_ReturnsRequest() {
        when(service.getById("sr-1")).thenReturn(testRequest);

        ResponseEntity<ServiceRequest> response = controller.getById("sr-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("sr-1", response.getBody().getId());
    }

    // ========== getByVehicleId() tests ==========

    @Test
    void getByVehicleId_ReturnsRequests() {
        when(service.getByVehicleId("vehicle-1")).thenReturn(Arrays.asList(testRequest));

        ResponseEntity<List<ServiceRequest>> response = controller.getByVehicleId("vehicle-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getByVehicleId_EmptyList() {
        when(service.getByVehicleId("vehicle-2")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ServiceRequest>> response = controller.getByVehicleId("vehicle-2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ========== getMyServiceRequests() tests ==========

    @Test
    void getMyServiceRequests_ReturnsRequests() {
        when(service.getByUserId("user-1")).thenReturn(Arrays.asList(testRequest));

        ResponseEntity<List<ServiceRequest>> response = controller.getMyServiceRequests("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getMyServiceRequests_EmptyList() {
        when(service.getByUserId("user-2")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ServiceRequest>> response = controller.getMyServiceRequests("user-2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
