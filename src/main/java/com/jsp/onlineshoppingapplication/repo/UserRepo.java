package com.jsp.onlineshoppingapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.onlineshoppingapplication.entity.User;


public interface UserRepo extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

}
