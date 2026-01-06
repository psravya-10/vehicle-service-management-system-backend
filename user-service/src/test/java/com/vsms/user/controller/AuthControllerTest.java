package com.vsms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.user.dto.*;
import com.vsms.user.enums.Role;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.exception.GlobalExceptionHandler;
import com.vsms.user.service.AuthService;
import com.vsms.user.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/auth/register/customer")
    class RegisterCustomerTests {

        @Test
        @DisplayName("Should register customer successfully")
        void registerCustomer_Success() throws Exception {
            RegisterCustomerRequest req = new RegisterCustomerRequest();
            req.setName("Test Customer");
            req.setEmail("test@example.com");
            req.setPassword("password123");
            req.setPhone("1234567890");
            req.setPincode("123456");

            when(authService.registerCustomer(any(RegisterCustomerRequest.class))).thenReturn("customer-123");

            mockMvc.perform(post("/api/auth/register/customer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("customer-123"));

            verify(authService).registerCustomer(any(RegisterCustomerRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when email already exists")
        void registerCustomer_EmailExists_Returns400() throws Exception {
            RegisterCustomerRequest req = new RegisterCustomerRequest();
            req.setName("Test Customer");
            req.setEmail("existing@example.com");
            req.setPassword("password123");
            req.setPhone("1234567890");
            req.setPincode("123456");

            when(authService.registerCustomer(any(RegisterCustomerRequest.class)))
                    .thenThrow(new BusinessException("Email already registered"));

            mockMvc.perform(post("/api/auth/register/customer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already registered"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/register/staff")
    class RegisterStaffTests {

        @Test
        @DisplayName("Should register staff successfully")
        void registerStaff_Success() throws Exception {
            RegisterStaffRequest req = new RegisterStaffRequest();
            req.setName("Test Tech");
            req.setEmail("tech@example.com");
            req.setPassword("password123");
            req.setPhone("1234567890");
            req.setPincode("123456");
            req.setRole(Role.TECHNICIAN);

            doNothing().when(authService).registerStaff(any(RegisterStaffRequest.class));

            mockMvc.perform(post("/api/auth/register/staff")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());

            verify(authService).registerStaff(any(RegisterStaffRequest.class));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void login_Success() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("test@example.com");
            req.setPassword("password123");

            LoginResponse response = new LoginResponse("jwt-token", Role.CUSTOMER);
            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"))
                    .andExpect(jsonPath("$.role").value("CUSTOMER"));
        }
    }

    @Nested
    @DisplayName("GET /api/auth/profile")
    class GetProfileTests {

        @Test
        @DisplayName("Should get profile successfully")
        void getProfile_Success() throws Exception {
            CustomerResponse response = CustomerResponse.builder()
                    .id("customer-1")
                    .name("Test Customer")
                    .email("test@example.com")
                    .role(Role.CUSTOMER)
                    .build();

            when(customerService.getByEmail("test@example.com")).thenReturn(response);

            // Create a mock Authentication
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("test@example.com");

            // For controller tests with authentication, we need to test through the service
            CustomerResponse result = authController.getMyProfile(mockAuth);

            assertEquals("customer-1", result.getId());
            assertEquals("Test Customer", result.getName());
            verify(customerService).getByEmail("test@example.com");
        }
    }

    @Nested
    @DisplayName("PUT /api/auth/change-password")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePassword_Success() throws Exception {
            Authentication mockAuth = mock(Authentication.class);
            when(mockAuth.getName()).thenReturn("test@example.com");

            ChangePasswordRequest req = new ChangePasswordRequest();
            req.setCurrentPassword("oldPassword");
            req.setNewPassword("newPassword123");

            doNothing().when(authService).changePassword(eq("test@example.com"), any(ChangePasswordRequest.class));

            authController.changePassword(mockAuth, req);

            verify(authService).changePassword(eq("test@example.com"), any(ChangePasswordRequest.class));
        }
    }
}
