package com.vsms.servicerequest.service;

import com.vsms.servicerequest.dto.feign.VehicleFeignResponse;
import com.vsms.servicerequest.exception.ServiceUnavailableException;
import com.vsms.servicerequest.feign.VehicleFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceClientTest {

    @Mock
    private VehicleFeignClient vehicleFeignClient;

    @InjectMocks
    private VehicleServiceClient vehicleServiceClient;


    @Test
    void getVehicleById_ReturnsVehicle() {
        VehicleFeignResponse vehicle = new VehicleFeignResponse();
        when(vehicleFeignClient.getVehicleById("v-1")).thenReturn(vehicle);

        VehicleFeignResponse result = vehicleServiceClient.getVehicleById("v-1");

        assertNotNull(result);
        verify(vehicleFeignClient).getVehicleById("v-1");
    }


    @Test
    void getVehicleByIdFallback_ThrowsServiceUnavailableException() {
        Exception testException = new RuntimeException("Service down");

        assertThrows(ServiceUnavailableException.class,
                () -> vehicleServiceClient.getVehicleByIdFallback("v-1", testException));
    }
}
