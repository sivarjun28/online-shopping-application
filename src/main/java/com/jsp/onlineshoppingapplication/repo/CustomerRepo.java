package com.jsp.onlineshoppingapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.onlineshoppingapplication.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer>{

}
