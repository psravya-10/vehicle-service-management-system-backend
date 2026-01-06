package com.vsms.user.controller;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.enums.Role;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.exception.GlobalExceptionHandler;
import com.vsms.user.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        customerResponse = CustomerResponse.builder()
                .id("customer-1")
                .name("Test Customer")
                .email("customer@test.com")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("GET /api/customer/{id} - Should return customer")
    void getCustomer_Success() throws Exception {
        when(customerService.getCustomerById("customer-1")).thenReturn(customerResponse);

        mockMvc.perform(get("/api/customer/customer-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("customer-1"))
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.email").value("customer@test.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(customerService).getCustomerById("customer-1");
    }

    @Test
    @DisplayName("GET /api/customer/{id} - Should return 400 when not found")
    void getCustomer_NotFound_Returns400() throws Exception {
        when(customerService.getCustomerById("nonexistent"))
                .thenThrow(new BusinessException("User not found"));

        mockMvc.perform(get("/api/customer/nonexistent"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
