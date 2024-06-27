package com.jsp.onlineshoppingapplication.mapper;

import org.springframework.stereotype.Component;

import com.jsp.onlineshoppingapplication.entity.User;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;

import onlineshoppingapplication.enums.UserRole;

@Component
public class UserMapper {

	public User mapToUser(com.jsp.onlineshoppingapplication.requestdto.UserRequest userRequest, User user) {
		user.setUsername(userRequest.getUsername());
		user.setEmail(userRequest.getEmail());
		user.setPassword(userRequest.getPassword());
		user.setUserRole(userRequest.getUserRole());
		return user;
		
	}
	
	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.userRole(user.getUserRole())
			.isEmailVerified(user.isEmailVerified())
			.isDeleted(user.isDeleted())
			.build();
			
	}
}
