package com.vsms.vehicle.controller;

import com.vsms.vehicle.dto.VehicleResponse;
import com.vsms.vehicle.entity.VehicleType;
import com.vsms.vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerVehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private ManagerVehicleController managerVehicleController;

    private VehicleResponse testVehicleResponse;

    @BeforeEach
    void setUp() {
        testVehicleResponse = VehicleResponse.builder()
                .id("vehicle-1")
                .userId("user-1")
                .registrationNumber("KA01AB1234")
                .brand("Toyota")
                .model("Camry")
                .vehicleType(VehicleType.CAR)
                .manufactureYear(2020)
                .build();
    }

    @Nested
    @DisplayName("getAllVehicles Tests")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("Should return all vehicles")
        void getAllVehicles_ReturnsVehicles() {
            when(vehicleService.getAllVehicles()).thenReturn(Arrays.asList(testVehicleResponse));

            ResponseEntity<List<VehicleResponse>> response = managerVehicleController.getAllVehicles();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            verify(vehicleService).getAllVehicles();
        }
    }

    @Nested
    @DisplayName("getVehicleById Tests")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("Should return vehicle by id")
        void getVehicleById_ReturnsVehicle() {
            when(vehicleService.getVehicleById("vehicle-1")).thenReturn(testVehicleResponse);

            ResponseEntity<VehicleResponse> response = managerVehicleController.getVehicleById("vehicle-1");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("vehicle-1", response.getBody().getId());
            assertEquals("Toyota", response.getBody().getBrand());
        }
    }
}
