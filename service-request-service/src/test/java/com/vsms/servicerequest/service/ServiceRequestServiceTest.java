package com.vsms.servicerequest.service;

import com.vsms.servicerequest.dto.AssignServiceRequestDto;
import com.vsms.servicerequest.dto.CreateServiceRequestDto;
import com.vsms.servicerequest.dto.NotificationEvent;
import com.vsms.servicerequest.entity.*;
import com.vsms.servicerequest.messaging.NotificationPublisher;
import com.vsms.servicerequest.repository.ServiceBayRepository;
import com.vsms.servicerequest.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestRepository requestRepo;

    @Mock
    private ServiceBayRepository bayRepo;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private BillingService billingService;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    private ServiceRequest testRequest;
    private ServiceBay testBay;
    private CreateServiceRequestDto createDto;
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

        testBay = ServiceBay.builder()
                .id("bay-1")
                .bayCode("BAY-01")
                .status(BayStatus.AVAILABLE)
                .build();

        createDto = new CreateServiceRequestDto();
        createDto.setUserId("user-1");
        createDto.setVehicleId("vehicle-1");
        createDto.setIssueDescription("Test issue");
        createDto.setPriority(ServicePriority.NORMAL);

        assignDto = new AssignServiceRequestDto();
        assignDto.setTechnicianId("tech-1");
        assignDto.setBayId("bay-1");
    }

    @Test
    void create_VehicleAlreadyInService_ThrowsException() {
        ServiceRequest activeRequest = ServiceRequest.builder()
                .id("sr-existing")
                .vehicleId("vehicle-1")
                .status(ServiceStatus.ASSIGNED)
                .build();
        when(requestRepo.findByVehicleId(anyString())).thenReturn(Arrays.asList(activeRequest));

        assertThrows(RuntimeException.class, () -> serviceRequestService.create(createDto));
    }

    @Test
    void create_VehicleHasClosedRequest_Success() {
        ServiceRequest closedRequest = ServiceRequest.builder()
                .id("sr-closed")
                .vehicleId("vehicle-1")
                .status(ServiceStatus.CLOSED)
                .build();
        when(requestRepo.findByVehicleId(anyString())).thenReturn(Arrays.asList(closedRequest));
        when(requestRepo.save(any(ServiceRequest.class))).thenReturn(testRequest);

        ServiceRequest result = serviceRequestService.create(createDto);

        assertNotNull(result);
        verify(requestRepo).save(any(ServiceRequest.class));
    }


    @Test
    void assign_Success() {
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(bayRepo.findById("bay-1")).thenReturn(Optional.of(testBay));
        when(userServiceClient.getUserEmail(anyString())).thenReturn("test@example.com");

        serviceRequestService.assign("sr-1", assignDto);

        verify(bayRepo).save(any(ServiceBay.class));
        verify(userServiceClient).updateTechnicianStatus("tech-1", false);
        verify(requestRepo).save(any(ServiceRequest.class));
        verify(notificationPublisher).publish(any(NotificationEvent.class));
    }

    @Test
    void assign_AlreadyAssigned_ThrowsException() {
        testRequest.setStatus(ServiceStatus.ASSIGNED);
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));

        assertThrows(RuntimeException.class, () -> serviceRequestService.assign("sr-1", assignDto));
    }

    @Test
    void assign_BayNotFound_ThrowsException() {
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(bayRepo.findById("bay-1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> serviceRequestService.assign("sr-1", assignDto));
    }

    @Test
    void assign_BayBusy_ThrowsException() {
        testBay.setStatus(BayStatus.BUSY);
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(bayRepo.findById("bay-1")).thenReturn(Optional.of(testBay));

        assertThrows(RuntimeException.class, () -> serviceRequestService.assign("sr-1", assignDto));
    }

    @Test
    void closeService_Success() {
        testRequest.setStatus(ServiceStatus.COMPLETED);
        testRequest.setTechnicianId("tech-1");
        testRequest.setBayId("bay-1");
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(bayRepo.findById("bay-1")).thenReturn(Optional.of(testBay));
        when(userServiceClient.getUserEmail(anyString())).thenReturn("test@example.com");

        serviceRequestService.closeService("sr-1", 100.0);

        verify(bayRepo).save(any(ServiceBay.class));
        verify(userServiceClient).updateTechnicianStatus("tech-1", true);
        verify(requestRepo).save(any(ServiceRequest.class));
        verify(billingService).generateInvoice("sr-1");
        verify(notificationPublisher).publish(any(NotificationEvent.class));
    }

    @Test
    void closeService_RequestNotFound_ThrowsException() {
        when(requestRepo.findById("sr-1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> serviceRequestService.closeService("sr-1", 100.0));
    }

    @Test
    void closeService_ZeroLabourCharges_ThrowsException() {
        testRequest.setStatus(ServiceStatus.COMPLETED);
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));

        assertThrows(RuntimeException.class, () -> serviceRequestService.closeService("sr-1", 0.0));
    }


    @Test
    void addUsedPart_Success_NewPartsList() {
        testRequest.setPartsUsed(null);
        UsedPart part = UsedPart.builder()
                .partName("Oil Filter")
                .quantity(1)
                .unitPrice(50.0)
                .totalPrice(50.0)
                .build();
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(requestRepo.save(any(ServiceRequest.class))).thenReturn(testRequest);

        serviceRequestService.addUsedPart("sr-1", part);

        verify(requestRepo).save(any(ServiceRequest.class));
    }

    @Test
    void addUsedPart_Success_ExistingPartsList() {
        UsedPart existingPart = UsedPart.builder()
                .partName("Brake Pad")
                .quantity(2)
                .unitPrice(100.0)
                .totalPrice(200.0)
                .build();
        testRequest.setPartsUsed(new ArrayList<>(Arrays.asList(existingPart)));
        
        UsedPart newPart = UsedPart.builder()
                .partName("Oil Filter")
                .quantity(1)
                .unitPrice(50.0)
                .totalPrice(50.0)
                .build();
        when(requestRepo.findById("sr-1")).thenReturn(Optional.of(testRequest));
        when(requestRepo.save(any(ServiceRequest.class))).thenReturn(testRequest);

        serviceRequestService.addUsedPart("sr-1", newPart);

        ArgumentCaptor<ServiceRequest> captor = ArgumentCaptor.forClass(ServiceRequest.class);
        verify(requestRepo).save(captor.capture());
        assertEquals(250.0, captor.getValue().getPartsTotal());
    }

    @Test
    void addUsedPart_RequestNotFound_ThrowsException() {
        UsedPart part = new UsedPart();
        when(requestRepo.findById("sr-1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> serviceRequestService.addUsedPart("sr-1", part));
    }


    @Test
    void getByVehicleId_ReturnsRequests() {
        when(requestRepo.findByVehicleId("vehicle-1")).thenReturn(Arrays.asList(testRequest));

        List<ServiceRequest> result = serviceRequestService.getByVehicleId("vehicle-1");

        assertEquals(1, result.size());
    }


    @Test
    void getByUserId_ReturnsRequests() {
        when(requestRepo.findByUserId("user-1")).thenReturn(Arrays.asList(testRequest));

        List<ServiceRequest> result = serviceRequestService.getByUserId("user-1");

        assertEquals(1, result.size());
    }

    @Test
    void getByTechnicianId_ReturnsRequests() {
        testRequest.setTechnicianId("tech-1");
        when(requestRepo.findByTechnicianId("tech-1")).thenReturn(Arrays.asList(testRequest));

        List<ServiceRequest> result = serviceRequestService.getByTechnicianId("tech-1");

        assertEquals(1, result.size());
    }


    @Test
    void getTechnicianWorkload_ReturnsCount() {
        List<ServiceStatus> activeStatuses = List.of(ServiceStatus.ASSIGNED, ServiceStatus.IN_PROGRESS);
        when(requestRepo.countByTechnicianIdAndStatusIn("tech-1", activeStatuses)).thenReturn(3L);

        long result = serviceRequestService.getTechnicianWorkload("tech-1");

        assertEquals(3L, result);
    }

}
