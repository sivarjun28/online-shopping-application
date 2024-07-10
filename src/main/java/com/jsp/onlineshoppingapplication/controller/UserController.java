package com.jsp.onlineshoppingapplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.onlineshoppingapplication.requestdto.AuthRequest;
import com.jsp.onlineshoppingapplication.requestdto.OtpVerficationRequest;
import com.jsp.onlineshoppingapplication.requestdto.UserRequest;
import com.jsp.onlineshoppingapplication.responsedto.AuthResponse;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;
import com.jsp.onlineshoppingapplication.security.JWTService;
import com.jsp.onlineshoppingapplication.service.UserService;
import com.jsp.onlineshoppingapplication.util.ResponseStructure;
import com.jsp.onlineshoppingapplication.util.SimpleStructure;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import onlineshoppingapplication.enums.UserRole;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class UserController {

	private UserService userService;

	private JWTService jwtService;

	@PostMapping("/register/seller")
	public ResponseEntity<ResponseStructure<UserResponse>> registerSeller(@Valid @RequestBody UserRequest userRequest) {

		return userService.registerUser(userRequest, UserRole.SELLER);
	}

	@PostMapping("/register/customer")
	public ResponseEntity<ResponseStructure<UserResponse>> registerCustomer(
			@Valid @RequestBody UserRequest userRequest) {

		return userService.registerUser(userRequest, UserRole.CUSTOMER);
	}

	@PostMapping("users/otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyUserEmail(
			@RequestBody OtpVerficationRequest otpVerficationRequest) {
		return userService.verifyUserEmail(otpVerficationRequest);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> jwtLogin(@RequestBody AuthRequest authRequest)  {
		return userService.jwtLogin(authRequest);
	}
	
	@PostMapping("/refresh-login")
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(@CookieValue(value = "rt",required = false) String refreshToken) {
		return  userService.refreshLogin(refreshToken);
	}
	
	@PostMapping("/test")
	public String test() {
		 return "success";
	}
	@PostMapping("/logout")
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(
			@CookieValue(value = "rt", required = false) String refreshToken,
			@CookieValue(value = "at", required = false) String accessToken) {
		return userService.logout(refreshToken, accessToken);

	}

	@PostMapping("//logout-from-other-devices")
	public ResponseEntity<SimpleStructure> logoutFromOtherDevices(
			@CookieValue(value = "rt", required = false) String refreshToken,
			@CookieValue(value = "at", required = false) String accessToken) {
		return userService.logoutFromOtherDevices(refreshToken, accessToken);
	}

	@PostMapping("/logout-all")
	public ResponseEntity<SimpleStructure> logoutFromAllDevices(@CookieValue(value="at",required=false) String accessToken) {
		return userService.logoutFromAllDevices(accessToken);

	}

}
