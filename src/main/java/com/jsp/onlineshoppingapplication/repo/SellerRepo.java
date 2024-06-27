package com.jsp.onlineshoppingapplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.onlineshoppingapplication.entity.Seller;

public interface SellerRepo extends JpaRepository<Seller, Integer>{

}
