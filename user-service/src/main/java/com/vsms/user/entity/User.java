package com.vsms.user.entity;

import com.vsms.user.enums.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotNull
    private Role role;

    private UserStatus status;

    private AvailabilityStatus availability;

    @NotBlank(message = "Phone is mandatory")
    private String phone;

    @NotBlank(message = "Pincode is mandatory")
    private String pincode;
}
