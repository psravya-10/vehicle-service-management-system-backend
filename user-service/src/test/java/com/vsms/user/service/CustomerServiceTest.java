package com.vsms.user.service;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.entity.User;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private CustomerService customerService;

    private User testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = User.builder()
                .id("customer-1")
                .name("Test Customer")
                .email("customer@test.com")
                .phone("1234567890")
                .pincode("123456")
                .role(Role.CUSTOMER)
                .status(UserStatus.APPROVED)
                .build();
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void getCustomerById_Success() {
        when(repo.findById("customer-1")).thenReturn(Optional.of(testCustomer));

        CustomerResponse response = customerService.getCustomerById("customer-1");

        assertNotNull(response);
        assertEquals("customer-1", response.getId());
        assertEquals("Test Customer", response.getName());
        assertEquals("customer@test.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        verify(repo).findById("customer-1");
    }

    @Test
    @DisplayName("Should throw exception when customer not found by ID")
    void getCustomerById_NotFound_ThrowsException() {
        when(repo.findById("nonexistent")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> customerService.getCustomerById("nonexistent"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get customer by email successfully")
    void getByEmail_Success() {
        when(repo.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));

        CustomerResponse response = customerService.getByEmail("customer@test.com");

        assertNotNull(response);
        assertEquals("customer-1", response.getId());
        assertEquals("Test Customer", response.getName());
        assertEquals("customer@test.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        verify(repo).findByEmail("customer@test.com");
    }

    @Test
    @DisplayName("Should throw exception when customer not found by email")
    void getByEmail_NotFound_ThrowsException() {
        when(repo.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> customerService.getByEmail("nonexistent@test.com"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get email by user ID successfully")
    void getEmailByUserId_Success() {
        when(repo.findById("customer-1")).thenReturn(Optional.of(testCustomer));

        String email = customerService.getEmailByUserId("customer-1");

        assertEquals("customer@test.com", email);
        verify(repo).findById("customer-1");
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found for email lookup")
    void getEmailByUserId_NotFound_ThrowsRuntimeException() {
        when(repo.findById("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerService.getEmailByUserId("nonexistent"));

        assertEquals("User not found", exception.getMessage());
    }
}
