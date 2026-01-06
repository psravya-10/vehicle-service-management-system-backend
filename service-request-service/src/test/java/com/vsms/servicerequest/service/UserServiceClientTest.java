package com.vsms.servicerequest.service;

import com.vsms.servicerequest.dto.feign.TechnicianFeignResponse;
import com.vsms.servicerequest.exception.ServiceUnavailableException;
import com.vsms.servicerequest.feign.UserFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceClientTest {

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private UserServiceClient userServiceClient;

    @Test
    void getAvailableTechnicians_ReturnsList() {
        TechnicianFeignResponse tech = new TechnicianFeignResponse();
        when(userFeignClient.getAvailableTechnicians()).thenReturn(Arrays.asList(tech));

        List<TechnicianFeignResponse> result = userServiceClient.getAvailableTechnicians();

        assertEquals(1, result.size());
        verify(userFeignClient).getAvailableTechnicians();
    }

    @Test
    void getAvailableTechnicians_ReturnsEmptyList() {
        when(userFeignClient.getAvailableTechnicians()).thenReturn(Collections.emptyList());

        List<TechnicianFeignResponse> result = userServiceClient.getAvailableTechnicians();

        assertTrue(result.isEmpty());
    }


    @Test
    void getAvailableTechniciansFallback_ThrowsServiceUnavailableException() {
        Exception testException = new RuntimeException("Service down");

        assertThrows(ServiceUnavailableException.class,
                () -> userServiceClient.getAvailableTechniciansFallback(testException));
    }


    @Test
    void updateTechnicianStatus_Success() {
        doNothing().when(userFeignClient).updateTechnicianStatus("tech-1", true);

        userServiceClient.updateTechnicianStatus("tech-1", true);

        verify(userFeignClient).updateTechnicianStatus("tech-1", true);
    }

    @Test
    void updateTechnicianStatus_SetUnavailable() {
        doNothing().when(userFeignClient).updateTechnicianStatus("tech-1", false);

        userServiceClient.updateTechnicianStatus("tech-1", false);

        verify(userFeignClient).updateTechnicianStatus("tech-1", false);
    }

    @Test
    void updateTechnicianStatusFallback_ThrowsServiceUnavailableException() {
        Exception testException = new RuntimeException("Service down");

        assertThrows(ServiceUnavailableException.class,
                () -> userServiceClient.updateTechnicianStatusFallback("tech-1", true, testException));
    }


    @Test
    void getUserEmail_ReturnsEmail() {
        when(userFeignClient.getUserEmail("user-1")).thenReturn("test@example.com");

        String result = userServiceClient.getUserEmail("user-1");

        assertEquals("test@example.com", result);
        verify(userFeignClient).getUserEmail("user-1");
    }


    @Test
    void getUserEmailFallback_ThrowsServiceUnavailableException() {
        Exception testException = new RuntimeException("Service down");

        assertThrows(ServiceUnavailableException.class,
                () -> userServiceClient.getUserEmailFallback("user-1", testException));
    }
}
