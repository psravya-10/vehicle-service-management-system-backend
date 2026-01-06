package com.vsms.vehicle.service;

import com.vsms.vehicle.dto.CreateVehicleRequest;
import com.vsms.vehicle.dto.UpdateVehicleRequest;
import com.vsms.vehicle.dto.VehicleResponse;
import com.vsms.vehicle.entity.Vehicle;
import com.vsms.vehicle.entity.VehicleType;
import com.vsms.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle testVehicle;
    private CreateVehicleRequest createRequest;
    private UpdateVehicleRequest updateRequest;

    @BeforeEach
    void setUp() {
        testVehicle = Vehicle.builder()
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
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setManufactureYear(2021);
    }

    @Nested
    @DisplayName("createVehicle Tests")
    class CreateVehicleTests {

        @Test
        @DisplayName("Should create vehicle successfully")
        void createVehicle_Success() {
            when(vehicleRepository.findByRegistrationNumber("KA01AB1234")).thenReturn(Optional.empty());
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
                Vehicle saved = invocation.getArgument(0);
                saved.setId("new-vehicle-id");
                return saved;
            });

            VehicleResponse response = vehicleService.createVehicle(createRequest);

            assertNotNull(response);
            assertEquals("new-vehicle-id", response.getId());
            assertEquals("Toyota", response.getBrand());
            verify(vehicleRepository).save(any(Vehicle.class));
        }
    }

    @Nested
    @DisplayName("getAllVehicles Tests")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("Should return all vehicles")
        void getAllVehicles_ReturnsVehicles() {
            when(vehicleRepository.findAll()).thenReturn(Arrays.asList(testVehicle));

            List<VehicleResponse> result = vehicleService.getAllVehicles();

            assertEquals(1, result.size());
            assertEquals("Toyota", result.get(0).getBrand());
            verify(vehicleRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getVehicleById Tests")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("Should return vehicle when found")
        void getVehicleById_Found_ReturnsVehicle() {
            when(vehicleRepository.findById("vehicle-1")).thenReturn(Optional.of(testVehicle));

            VehicleResponse response = vehicleService.getVehicleById("vehicle-1");

            assertNotNull(response);
            assertEquals("Toyota", response.getBrand());
        }
    }

    @Nested
    @DisplayName("getVehiclesByUser Tests")
    class GetVehiclesByUserTests {

        @Test
        @DisplayName("Should return vehicles for user")
        void getVehiclesByUser_ReturnsVehicles() {
            when(vehicleRepository.findByUserId("user-1")).thenReturn(Arrays.asList(testVehicle));

            List<VehicleResponse> result = vehicleService.getVehiclesByUser("user-1");

            assertEquals(1, result.size());
            assertEquals("user-1", result.get(0).getUserId());
        }
    }

    @Nested
    @DisplayName("updateVehicle Tests")
    class UpdateVehicleTests {

        @Test
        @DisplayName("Should update vehicle successfully")
        void updateVehicle_Success() {
            when(vehicleRepository.findById("vehicle-1")).thenReturn(Optional.of(testVehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

            VehicleResponse response = vehicleService.updateVehicle("vehicle-1", updateRequest);

            assertNotNull(response);
            assertEquals("Honda", response.getBrand());
            assertEquals("Accord", response.getModel());
            assertEquals(2021, response.getManufactureYear());
        }
    }
}

