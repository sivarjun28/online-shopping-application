package com.jsp.onlineshoppingapplication.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.onlineshoppingapplication.entity.User;
import com.jsp.onlineshoppingapplication.requestdto.OtpVerficationRequest;
import com.jsp.onlineshoppingapplication.requestdto.UserRequest;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;
import com.jsp.onlineshoppingapplication.service.UserService;
import com.jsp.onlineshoppingapplication.util.ResponseStructure;
import com.jsp.onlineshoppingapplication.util.SimpleStructure;

import jakarta.validation.Valid;
import onlineshoppingapplication.enums.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1")
public class UserController {
@Autowired
private UserService userService;
@PostMapping("seller/register")
public ResponseEntity<ResponseStructure<UserResponse>> registerSeller(@Valid @RequestBody UserRequest userRequest) {
   
    
    return userService.registerUser(userRequest, UserRole.SELLER);
}
@PostMapping("customer/register")
public ResponseEntity<ResponseStructure<UserResponse>> registerCustomer(@Valid @RequestBody UserRequest userRequest) {
   
    
    return userService.registerUser(userRequest,UserRole.CUSTOMER);
}

@PostMapping("users/otp")
public	ResponseEntity<ResponseStructure<UserResponse>> verifyUserEmail(@RequestBody OtpVerficationRequest otpVerficationRequest) {
   
   return userService.verifyUserEmail(otpVerficationRequest);
    
}

	
}
