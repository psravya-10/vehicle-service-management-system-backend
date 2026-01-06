package com.vsms.user.service;

import com.vsms.user.dto.*;
import com.vsms.user.entity.User;
import com.vsms.user.enums.AvailabilityStatus;
import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;
import com.vsms.user.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testCustomer;
    private User testTechnician;
    private User testPendingUser;
    private User testRejectedUser;

    @BeforeEach
    void setUp() {
        testCustomer = User.builder()
                .id("customer-1")
                .name("Test Customer")
                .email("customer@test.com")
                .password("encodedPassword")
                .phone("1234567890")
                .pincode("123456")
                .role(Role.CUSTOMER)
                .status(UserStatus.APPROVED)
                .build();

        testTechnician = User.builder()
                .id("tech-1")
                .name("Test Technician")
                .email("tech@test.com")
                .password("encodedPassword")
                .phone("1234567890")
                .pincode("123456")
                .role(Role.TECHNICIAN)
                .status(UserStatus.APPROVED)
                .availability(AvailabilityStatus.AVAILABLE)
                .build();

        testPendingUser = User.builder()
                .id("pending-1")
                .name("Pending User")
                .email("pending@test.com")
                .password("encodedPassword")
                .role(Role.TECHNICIAN)
                .status(UserStatus.PENDING)
                .build();

        testRejectedUser = User.builder()
                .id("rejected-1")
                .name("Rejected User")
                .email("rejected@test.com")
                .password("encodedPassword")
                .role(Role.TECHNICIAN)
                .status(UserStatus.REJECTED)
                .build();
    }

    @Nested
    @DisplayName("registerCustomer Tests")
    class RegisterCustomerTests {

        @Test
        @DisplayName("Should register customer successfully")
        void registerCustomer_Success() {
            RegisterCustomerRequest req = new RegisterCustomerRequest();
            req.setName("New Customer");
            req.setEmail("new@test.com");
            req.setPassword("password123");
            req.setPhone("9876543210");
            req.setPincode("654321");

            when(repo.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(encoder.encode("password123")).thenReturn("encodedPassword");
            when(repo.save(any(User.class))).thenAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setId("new-customer-id");
                return saved;
            });

            String result = authService.registerCustomer(req);

            assertNotNull(result);
            assertEquals("new-customer-id", result);
            verify(repo).findByEmail("new@test.com");
            verify(encoder).encode("password123");
            verify(repo).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void registerCustomer_EmailExists_ThrowsException() {
            RegisterCustomerRequest req = new RegisterCustomerRequest();
            req.setEmail("customer@test.com");

            when(repo.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.registerCustomer(req));

            assertEquals("Email already registered", exception.getMessage());
            verify(repo).findByEmail("customer@test.com");
            verify(repo, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("registerStaff Tests")
    class RegisterStaffTests {

        @Test
        @DisplayName("Should register technician successfully")
        void registerStaff_Technician_Success() {
            RegisterStaffRequest req = new RegisterStaffRequest();
            req.setName("New Tech");
            req.setEmail("newtech@test.com");
            req.setPassword("password123");
            req.setPhone("9876543210");
            req.setPincode("654321");
            req.setRole(Role.TECHNICIAN);

            when(repo.findByEmail("newtech@test.com")).thenReturn(Optional.empty());
            when(encoder.encode("password123")).thenReturn("encodedPassword");

            assertDoesNotThrow(() -> authService.registerStaff(req));

            verify(repo).save(argThat(user ->
                    user.getRole() == Role.TECHNICIAN &&
                    user.getStatus() == UserStatus.PENDING &&
                    user.getAvailability() == AvailabilityStatus.AVAILABLE
            ));
        }

        @Test
        @DisplayName("Should register manager successfully")
        void registerStaff_Manager_Success() {
            RegisterStaffRequest req = new RegisterStaffRequest();
            req.setName("New Manager");
            req.setEmail("newmanager@test.com");
            req.setPassword("password123");
            req.setPhone("9876543210");
            req.setPincode("654321");
            req.setRole(Role.MANAGER);

            when(repo.findByEmail("newmanager@test.com")).thenReturn(Optional.empty());
            when(encoder.encode("password123")).thenReturn("encodedPassword");

            assertDoesNotThrow(() -> authService.registerStaff(req));

            verify(repo).save(argThat(user ->
                    user.getRole() == Role.MANAGER &&
                    user.getStatus() == UserStatus.PENDING &&
                    user.getAvailability() == null
            ));
        }

        @Test
        @DisplayName("Should throw exception for CUSTOMER role")
        void registerStaff_CustomerRole_ThrowsException() {
            RegisterStaffRequest req = new RegisterStaffRequest();
            req.setRole(Role.CUSTOMER);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.registerStaff(req));

            assertEquals("Invalid role for staff registration", exception.getMessage());
            verify(repo, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for ADMIN role")
        void registerStaff_AdminRole_ThrowsException() {
            RegisterStaffRequest req = new RegisterStaffRequest();
            req.setRole(Role.ADMIN);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.registerStaff(req));

            assertEquals("Invalid role for staff registration", exception.getMessage());
            verify(repo, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void registerStaff_EmailExists_ThrowsException() {
            RegisterStaffRequest req = new RegisterStaffRequest();
            req.setEmail("tech@test.com");
            req.setRole(Role.TECHNICIAN);

            when(repo.findByEmail("tech@test.com")).thenReturn(Optional.of(testTechnician));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.registerStaff(req));

            assertEquals("Email already registered", exception.getMessage());
            verify(repo, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void login_Success() {
            LoginRequest req = new LoginRequest();
            req.setEmail("customer@test.com");
            req.setPassword("password123");

            when(repo.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));
            when(encoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(jwtUtil.generateToken("customer@test.com", "CUSTOMER")).thenReturn("jwt-token");

            LoginResponse response = authService.login(req);

            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            assertEquals(Role.CUSTOMER, response.getRole());
        }

        @Test
        @DisplayName("Should throw exception for invalid email")
        void login_InvalidEmail_ThrowsException() {
            LoginRequest req = new LoginRequest();
            req.setEmail("nonexistent@test.com");
            req.setPassword("password123");

            when(repo.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(req));

            assertEquals("Invalid credentials", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for wrong password")
        void login_WrongPassword_ThrowsException() {
            LoginRequest req = new LoginRequest();
            req.setEmail("customer@test.com");
            req.setPassword("wrongpassword");

            when(repo.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));
            when(encoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(req));

            assertEquals("Invalid credentials", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for pending user")
        void login_PendingUser_ThrowsException() {
            LoginRequest req = new LoginRequest();
            req.setEmail("pending@test.com");
            req.setPassword("password123");

            when(repo.findByEmail("pending@test.com")).thenReturn(Optional.of(testPendingUser));
            when(encoder.matches("password123", "encodedPassword")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(req));

            assertEquals("Your account is pending, need admin approval", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for rejected user")
        void login_RejectedUser_ThrowsException() {
            LoginRequest req = new LoginRequest();
            req.setEmail("rejected@test.com");
            req.setPassword("password123");

            when(repo.findByEmail("rejected@test.com")).thenReturn(Optional.of(testRejectedUser));
            when(encoder.matches("password123", "encodedPassword")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(req));

            assertEquals("Your account has been rejected", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("changePassword Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePassword_Success() {
            ChangePasswordRequest req = new ChangePasswordRequest();
            req.setCurrentPassword("password123");
            req.setNewPassword("newPassword456");

            when(repo.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));
            when(encoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(encoder.encode("newPassword456")).thenReturn("newEncodedPassword");

            assertDoesNotThrow(() -> authService.changePassword("customer@test.com", req));

            verify(repo).save(argThat(user -> user.getPassword().equals("newEncodedPassword")));
        }
        @Test
        @DisplayName("Should throw exception for incorrect current password")
        void changePassword_WrongCurrentPassword_ThrowsException() {
            ChangePasswordRequest req = new ChangePasswordRequest();
            req.setCurrentPassword("wrongPassword");
            req.setNewPassword("newPassword456");

            when(repo.findByEmail("customer@test.com")).thenReturn(Optional.of(testCustomer));
            when(encoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.changePassword("customer@test.com", req));

            assertEquals("Current password is incorrect", exception.getMessage());
            verify(repo, never()).save(any(User.class));
        }
    }
}
