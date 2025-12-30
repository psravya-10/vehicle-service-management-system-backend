package com.vsms.user.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterCustomerRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    @NotBlank
    private String pincode;
}
