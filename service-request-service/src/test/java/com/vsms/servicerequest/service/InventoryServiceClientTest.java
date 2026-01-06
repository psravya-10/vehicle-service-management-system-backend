package com.vsms.servicerequest.service;

import com.vsms.servicerequest.exception.ServiceUnavailableException;
import com.vsms.servicerequest.feign.InventoryFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceClientTest {

    @Mock
    private InventoryFeignClient inventoryFeignClient;

    @InjectMocks
    private InventoryServiceClient inventoryServiceClient;

    // ========== hasPendingRequests() tests ==========

    @Test
    void hasPendingRequests_ReturnsTrue() {
        when(inventoryFeignClient.hasPendingRequests("sr-1")).thenReturn(true);

        boolean result = inventoryServiceClient.hasPendingRequests("sr-1");

        assertTrue(result);
        verify(inventoryFeignClient).hasPendingRequests("sr-1");
    }

    @Test
    void hasPendingRequests_ReturnsFalse() {
        when(inventoryFeignClient.hasPendingRequests("sr-1")).thenReturn(false);

        boolean result = inventoryServiceClient.hasPendingRequests("sr-1");

        assertFalse(result);
    }

    // ========== hasPendingRequestsFallback() tests ==========

    @Test
    void hasPendingRequestsFallback_ThrowsServiceUnavailableException() {
        Exception testException = new RuntimeException("Service down");

        assertThrows(ServiceUnavailableException.class, 
                () -> inventoryServiceClient.hasPendingRequestsFallback("sr-1", testException));
    }
}
