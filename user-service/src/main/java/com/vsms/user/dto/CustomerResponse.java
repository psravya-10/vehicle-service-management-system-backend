package com.vsms.user.dto;

import com.vsms.user.enums.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
}

