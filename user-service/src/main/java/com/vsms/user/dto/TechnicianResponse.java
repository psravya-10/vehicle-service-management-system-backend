package com.vsms.user.dto;

import com.vsms.user.enums.AvailabilityStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponse {

    private String id;
    private String name;
    private String email;
    private AvailabilityStatus availability;
}
