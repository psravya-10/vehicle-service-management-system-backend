package com.vsms.servicerequest.dto;

import com.vsms.servicerequest.entity.ServiceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TechnicianUpdateServiceStatusDto {
	@NotNull
    private ServiceStatus status;  
	 @NotBlank
    private String remarks;
}
