package com.jsp.onlineshoppingapplication.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onlineshoppingapplication.enums.UserRole;

@Setter	
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
	private int userId;
	private String username;
	private String email;
	UserRole userRole;
	boolean isEmailVerified;
	boolean isDeleted;
}
