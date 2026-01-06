package com.vsms.vehicle.controller;

import com.vsms.vehicle.dto.CreateVehicleRequest;
import com.vsms.vehicle.dto.UpdateVehicleRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerVehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private CustomerVehicleController customerVehicleController;

    private VehicleResponse testVehicleResponse;
    private CreateVehicleRequest createRequest;
    private UpdateVehicleRequest updateRequest;

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

        createRequest = new CreateVehicleRequest();
        createRequest.setUserId("user-1");
        createRequest.setRegistrationNumber("KA01AB1234");
        createRequest.setBrand("Toyota");
        createRequest.setModel("Camry");
        createRequest.setVehicleType(VehicleType.CAR);
        createRequest.setManufactureYear(2020);

        updateRequest = new UpdateVehicleRequest();
        updateRequest.setBrand("Honda");
        updateRequest.setModel("Accord");
    }

    @Nested
    @DisplayName("createVehicle Tests")
    class CreateVehicleTests {

        @Test
        @DisplayName("Should create vehicle and return CREATED status")
        void createVehicle_Success() {
            when(vehicleService.createVehicle(any(CreateVehicleRequest.class))).thenReturn(testVehicleResponse);

            ResponseEntity<VehicleResponse> response = customerVehicleController.createVehicle(createRequest);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Toyota", response.getBody().getBrand());
        }
    }

    @Nested
    @DisplayName("getMyVehicles Tests")
    class GetMyVehiclesTests {

        @Test
        @DisplayName("Should return vehicles for user")
        void getMyVehicles_ReturnsVehicles() {
            when(vehicleService.getVehiclesByUser("user-1")).thenReturn(Arrays.asList(testVehicleResponse));

            ResponseEntity<List<VehicleResponse>> response = customerVehicleController.getMyVehicles("user-1");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
        }
    }

    @Nested
    @DisplayName("getVehicleById Tests")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("Should return vehicle by id")
        void getVehicleById_ReturnsVehicle() {
            when(vehicleService.getVehicleById("vehicle-1")).thenReturn(testVehicleResponse);

            ResponseEntity<VehicleResponse> response = customerVehicleController.getVehicleById("vehicle-1");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("vehicle-1", response.getBody().getId());
        }
    }

    @Nested
    @DisplayName("updateVehicle Tests")
    class UpdateVehicleTests {

        @Test
        @DisplayName("Should update vehicle successfully")
        void updateVehicle_Success() {
            VehicleResponse updatedResponse = VehicleResponse.builder()
                    .id("vehicle-1")
                    .brand("Honda")
                    .model("Accord")
                    .build();
            when(vehicleService.updateVehicle(eq("vehicle-1"), any(UpdateVehicleRequest.class))).thenReturn(updatedResponse);

            ResponseEntity<VehicleResponse> response = customerVehicleController.updateVehicle("vehicle-1", updateRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Honda", response.getBody().getBrand());
        }
    }
}
