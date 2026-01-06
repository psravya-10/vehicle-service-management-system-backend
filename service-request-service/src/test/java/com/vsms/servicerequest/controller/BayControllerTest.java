package com.vsms.servicerequest.controller;

import com.vsms.servicerequest.entity.BayStatus;
import com.vsms.servicerequest.entity.ServiceBay;
import com.vsms.servicerequest.repository.ServiceBayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BayControllerTest {

    @Mock
    private ServiceBayRepository repo;

    @InjectMocks
    private BayController controller;

    private ServiceBay testBay;

    @BeforeEach
    void setUp() {
        testBay = ServiceBay.builder()
                .id("bay-1")
                .bayCode("BAY-01")
                .status(BayStatus.AVAILABLE)
                .build();
    }

    @Test
    void allBays_ReturnsBays() {
        when(repo.findAll()).thenReturn(Arrays.asList(testBay));
        List<ServiceBay> result = controller.allBays();
        assertEquals(1, result.size());
    }

    @Test
    void availableBays_ReturnsAvailableBays() {
        when(repo.findByStatus(BayStatus.AVAILABLE)).thenReturn(Arrays.asList(testBay));
        List<ServiceBay> result = controller.availableBays();
        assertEquals(1, result.size());
    }
}
