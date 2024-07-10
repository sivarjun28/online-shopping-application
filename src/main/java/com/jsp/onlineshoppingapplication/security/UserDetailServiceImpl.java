package com.jsp.onlineshoppingapplication.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jsp.onlineshoppingapplication.repo.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService{

	private UserRepo userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.map(UserDetailImpl::new)
				.orElseThrow(()-> new UsernameNotFoundException("Authentication failed"));
	}

}
