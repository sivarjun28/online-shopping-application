package com.jsp.onlineshoppingapplication.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
	private int userId;
	private String username;
	private long accessExpiration;
	private long refreshExpiration;
	private String role;
}
