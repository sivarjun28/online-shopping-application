package com.jsp.onlineshoppingapplication.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jsp.onlineshoppingapplication.entity.User;
import com.jsp.onlineshoppingapplication.requestdto.UserRequest;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import onlineshoppingapplication.enums.UserRole;

@AllArgsConstructor
@Component
public class UserMapper {

	private final PasswordEncoder passwordEncoder;
	public User mapToUser(com.jsp.onlineshoppingapplication.requestdto.UserRequest userRequest, User user) {
		
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
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
	

	public User mapToUser(UserRequest userRequest, User user, UserRole userRole) {
        user = mapToUser(userRequest, user); 
        user.setUserRole(userRole); 
        
        return user;
    }
}
