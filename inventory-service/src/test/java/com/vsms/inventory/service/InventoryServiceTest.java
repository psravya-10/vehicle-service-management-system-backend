package com.vsms.inventory.service;

import com.vsms.inventory.dto.*;
import com.vsms.inventory.entity.*;
import com.vsms.inventory.exception.ResourceNotFoundException;
import com.vsms.inventory.repository.*;
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
class InventoryServiceTest {

    @Mock
    private SparePartRepository partRepo;

    @Mock
    private PartRequestRepository requestRepo;

    @Mock
    private ServiceRequestServiceClient serviceRequestServiceClient;

    @InjectMocks
    private InventoryService inventoryService;

    private SparePart testPart;
    private PartRequest testRequest;

    @BeforeEach
    void setUp() {
        testPart = SparePart.builder()
                .id("part-1")
                .name("Brake Pad")
                .category("Brakes")
                .availableQuantity(10)
                .unitPrice(50.0)
                .lowStockThreshold(3)
                .build();

        testRequest = PartRequest.builder()
                .id("request-1")
                .serviceRequestId("sr-1")
                .technicianId("tech-1")
                .partId("part-1")
                .quantity(2)
                .status(RequestStatus.REQUESTED)
                .build();
    }

    @Nested
    @DisplayName("addPart Tests")
    class AddPartTests {

        @Test
        @DisplayName("Should add part successfully")
        void addPart_Success() {
            CreateSparePartDto dto = new CreateSparePartDto();
            dto.setName("Oil Filter");
            dto.setCategory("Filters");
            dto.setAvailableQuantity(20);
            dto.setUnitPrice(15.0);

            when(partRepo.save(any(SparePart.class))).thenAnswer(invocation -> {
                SparePart saved = invocation.getArgument(0);
                saved.setId("new-part-id");
                return saved;
            });

            String result = inventoryService.addPart(dto);

            assertNotNull(result);
            assertEquals("new-part-id", result);
            verify(partRepo).save(any(SparePart.class));
        }
    }

    @Nested
    @DisplayName("requestPart Tests")
    class RequestPartTests {

        @Test
        @DisplayName("Should create part request successfully")
        void requestPart_Success() {
            CreatePartRequestDto dto = new CreatePartRequestDto();
            dto.setServiceRequestId("sr-1");
            dto.setTechnicianId("tech-1");
            dto.setPartId("part-1");
            dto.setQuantity(2);

            when(partRepo.findById("part-1")).thenReturn(Optional.of(testPart));
            when(requestRepo.save(any(PartRequest.class))).thenAnswer(invocation -> {
                PartRequest saved = invocation.getArgument(0);
                saved.setId("new-request-id");
                return saved;
            });

            String result = inventoryService.requestPart(dto);

            assertNotNull(result);
            assertEquals("new-request-id", result);
        }

        @Test
        @DisplayName("Should throw exception when part not found")
        void requestPart_PartNotFound_ThrowsException() {
            CreatePartRequestDto dto = new CreatePartRequestDto();
            dto.setPartId("nonexistent");

            when(partRepo.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> inventoryService.requestPart(dto));
        }
    }

    @Nested
    @DisplayName("approveRequest Tests")
    class ApproveRequestTests {

        @Test
        @DisplayName("Should approve request successfully")
        void approveRequest_Success() {
            when(requestRepo.findById("request-1")).thenReturn(Optional.of(testRequest));
            when(partRepo.findById("part-1")).thenReturn(Optional.of(testPart));

            inventoryService.approveRequest("request-1", "manager-1");

            ArgumentCaptor<SparePart> partCaptor = ArgumentCaptor.forClass(SparePart.class);
            verify(partRepo).save(partCaptor.capture());
            assertEquals(8, partCaptor.getValue().getAvailableQuantity());

            ArgumentCaptor<PartRequest> requestCaptor = ArgumentCaptor.forClass(PartRequest.class);
            verify(requestRepo).save(requestCaptor.capture());
            assertEquals(RequestStatus.APPROVED, requestCaptor.getValue().getStatus());
            assertEquals("manager-1", requestCaptor.getValue().getApprovedBy());

            verify(serviceRequestServiceClient).addUsedPart(eq("sr-1"), any(UsedPartDto.class));
        }

        @Test
        @DisplayName("Should throw exception when request not found")
        void approveRequest_RequestNotFound_ThrowsException() {
            when(requestRepo.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                    () -> inventoryService.approveRequest("nonexistent", "manager-1"));
        }

        @Test
        @DisplayName("Should throw exception when request already approved")
        void approveRequest_AlreadyApproved_ThrowsException() {
            testRequest.setStatus(RequestStatus.APPROVED);
            when(requestRepo.findById("request-1")).thenReturn(Optional.of(testRequest));

            assertThrows(IllegalStateException.class, 
                    () -> inventoryService.approveRequest("request-1", "manager-1"));
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock")
        void approveRequest_InsufficientStock_ThrowsException() {
            testRequest.setQuantity(20);
            when(requestRepo.findById("request-1")).thenReturn(Optional.of(testRequest));
            when(partRepo.findById("part-1")).thenReturn(Optional.of(testPart));

            assertThrows(IllegalStateException.class, 
                    () -> inventoryService.approveRequest("request-1", "manager-1"));
        }
    }

    @Nested
    @DisplayName("getAllParts Tests")
    class GetAllPartsTests {

        @Test
        @DisplayName("Should return all parts")
        void getAllParts_ReturnsParts() {
            when(partRepo.findAll()).thenReturn(Arrays.asList(testPart));

            List<SparePart> result = inventoryService.getAllParts();

            assertEquals(1, result.size());
            verify(partRepo).findAll();
        }
    }

    @Nested
    @DisplayName("hasPendingRequests Tests")
    class HasPendingRequestsTests {

        @Test
        @DisplayName("Should return true when pending requests exist")
        void hasPendingRequests_HasPending_ReturnsTrue() {
            when(requestRepo.findByServiceRequestId("sr-1"))
                    .thenReturn(Collections.singletonList(testRequest));

            boolean result = inventoryService.hasPendingRequests("sr-1");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when no pending requests")
        void hasPendingRequests_NoPending_ReturnsFalse() {
            testRequest.setStatus(RequestStatus.APPROVED);
            when(requestRepo.findByServiceRequestId("sr-1"))
                    .thenReturn(Collections.singletonList(testRequest));

            boolean result = inventoryService.hasPendingRequests("sr-1");

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getLowStockParts Tests")
    class GetLowStockPartsTests {

        @Test
        @DisplayName("Should return low stock parts")
        void getLowStockParts_ReturnsLowStock() {
            when(partRepo.findLowStockParts()).thenReturn(Arrays.asList(testPart));

            List<SparePart> result = inventoryService.getLowStockParts();

            assertEquals(1, result.size());
            verify(partRepo).findLowStockParts();
        }
    }

    @Nested
    @DisplayName("restockPart Tests")
    class RestockPartTests {

        @Test
        @DisplayName("Should restock part successfully")
        void restockPart_Success() {
            when(partRepo.findById("part-1")).thenReturn(Optional.of(testPart));

            inventoryService.restockPart("part-1", 5);

            ArgumentCaptor<SparePart> captor = ArgumentCaptor.forClass(SparePart.class);
            verify(partRepo).save(captor.capture());
            assertEquals(15, captor.getValue().getAvailableQuantity());
        }

        @Test
        @DisplayName("Should throw exception for invalid quantity")
        void restockPart_InvalidQuantity_ThrowsException() {
            assertThrows(IllegalArgumentException.class, 
                    () -> inventoryService.restockPart("part-1", 0));
        }

        @Test
        @DisplayName("Should throw exception when part not found")
        void restockPart_PartNotFound_ThrowsException() {
            when(partRepo.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                    () -> inventoryService.restockPart("nonexistent", 5));
        }
    }

    @Nested
    @DisplayName("updateThreshold Tests")
    class UpdateThresholdTests {

        @Test
        @DisplayName("Should update threshold successfully")
        void updateThreshold_Success() {
            when(partRepo.findById("part-1")).thenReturn(Optional.of(testPart));

            inventoryService.updateThreshold("part-1", 5);

            ArgumentCaptor<SparePart> captor = ArgumentCaptor.forClass(SparePart.class);
            verify(partRepo).save(captor.capture());
            assertEquals(5, captor.getValue().getLowStockThreshold());
        }

        @Test
        @DisplayName("Should throw exception for negative threshold")
        void updateThreshold_NegativeThreshold_ThrowsException() {
            assertThrows(IllegalArgumentException.class, 
                    () -> inventoryService.updateThreshold("part-1", -1));
        }

        @Test
        @DisplayName("Should throw exception when part not found")
        void updateThreshold_PartNotFound_ThrowsException() {
            when(partRepo.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, 
                    () -> inventoryService.updateThreshold("nonexistent", 5));
        }
    }

    @Nested
    @DisplayName("getAllPartRequests Tests")
    class GetAllPartRequestsTests {

        @Test
        @DisplayName("Should return all part requests")
        void getAllPartRequests_ReturnsRequests() {
            when(requestRepo.findAll()).thenReturn(Arrays.asList(testRequest));

            List<PartRequest> result = inventoryService.getAllPartRequests();

            assertEquals(1, result.size());
            verify(requestRepo).findAll();
        }
    }

    @Nested
    @DisplayName("getPartRequestsByStatus Tests")
    class GetPartRequestsByStatusTests {

        @Test
        @DisplayName("Should return requests by status")
        void getPartRequestsByStatus_ReturnsFiltered() {
            when(requestRepo.findByStatus(RequestStatus.REQUESTED))
                    .thenReturn(Arrays.asList(testRequest));

            List<PartRequest> result = inventoryService.getPartRequestsByStatus(RequestStatus.REQUESTED);

            assertEquals(1, result.size());
            verify(requestRepo).findByStatus(RequestStatus.REQUESTED);
        }
    }
}
