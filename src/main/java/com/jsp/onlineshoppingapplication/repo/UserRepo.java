package com.jsp.onlineshoppingapplication.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.onlineshoppingapplication.entity.User;


public interface UserRepo extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);
	
	public Optional<User> findByEmail(String username);

	public Optional<User> findByUsername(String username);

	

}
