package com.vsms.servicerequest.controller;

import com.vsms.servicerequest.entity.UsedPart;
import com.vsms.servicerequest.service.ServiceRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalServiceRequestControllerTest {

    @Mock
    private ServiceRequestService service;

    @InjectMocks
    private InternalServiceRequestController controller;

    // ========== addUsedPart() tests ==========

    @Test
    void addUsedPart_Success() {
        UsedPart usedPart = UsedPart.builder()
                .partName("Oil Filter")
                .quantity(1)
                .unitPrice(50.0)
                .totalPrice(50.0)
                .build();

        doNothing().when(service).addUsedPart("sr-1", usedPart);

        controller.addUsedPart("sr-1", usedPart);

        verify(service).addUsedPart("sr-1", usedPart);
    }

    @Test
    void addUsedPart_WithDifferentPart() {
        UsedPart usedPart = UsedPart.builder()
                .partName("Brake Pad")
                .quantity(4)
                .unitPrice(100.0)
                .totalPrice(400.0)
                .build();

        doNothing().when(service).addUsedPart("sr-2", usedPart);

        controller.addUsedPart("sr-2", usedPart);

        verify(service).addUsedPart("sr-2", usedPart);
    }
}
