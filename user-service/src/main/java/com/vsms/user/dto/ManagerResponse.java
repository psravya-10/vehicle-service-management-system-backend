package com.vsms.user.dto;

import com.vsms.user.enums.Role;
import com.vsms.user.enums.UserStatus;

public record ManagerResponse(
	    String id,
	    String name,
	    String email,
	    Role role,
	    UserStatus status
	) {}

