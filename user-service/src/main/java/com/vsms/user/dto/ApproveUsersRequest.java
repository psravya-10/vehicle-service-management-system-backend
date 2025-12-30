package com.vsms.user.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ApproveUsersRequest {
    @NotEmpty
    private List<String> userIds;
}

