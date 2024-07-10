package com.jsp.onlineshoppingapplication.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jsp.onlineshoppingapplication.entity.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailImpl implements UserDetails{

	private User user;
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}
}
