package com.vsms.servicerequest.dto;

import com.vsms.servicerequest.entity.ServiceStatus;
import lombok.Data;

@Data
public class TechnicianUpdateServiceStatusDto {
    private ServiceStatus status;  
    private String remarks;
}
