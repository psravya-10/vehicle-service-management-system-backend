package com.vsms.servicerequest.dto.feign;

import lombok.Data;

@Data
public class TechnicianFeignResponse {

    private String id;
    private String name;
    private String email;
    private String availability; // AVAILABLE / BUSY
}
