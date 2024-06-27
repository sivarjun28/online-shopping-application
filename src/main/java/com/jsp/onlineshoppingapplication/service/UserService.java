package com.jsp.onlineshoppingapplication.service;

import org.springframework.http.ResponseEntity;

import com.jsp.onlineshoppingapplication.requestdto.OtpVerficationRequest;
import com.jsp.onlineshoppingapplication.requestdto.UserRequest;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;
import com.jsp.onlineshoppingapplication.util.ResponseStructure;
import com.jsp.onlineshoppingapplication.util.SimpleStructure;

import onlineshoppingapplication.enums.UserRole;

public interface UserService {


	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest, UserRole userRole);

	ResponseEntity<ResponseStructure<UserResponse>> verifyUserEmail(OtpVerficationRequest otpVerficationRequest);

}
