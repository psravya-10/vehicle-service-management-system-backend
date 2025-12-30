package com.vsms.user.dto;

import com.vsms.user.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterStaffRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role; 

    @NotBlank
    private String phone;

    @NotBlank
    private String pincode;
}
