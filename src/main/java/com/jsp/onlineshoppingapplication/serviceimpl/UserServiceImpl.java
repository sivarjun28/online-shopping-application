package com.jsp.onlineshoppingapplication.serviceimpl;


import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.jsp.onlineshoppingapplication.entity.Customer;
import com.jsp.onlineshoppingapplication.entity.Seller;
import com.jsp.onlineshoppingapplication.entity.User;
import com.jsp.onlineshoppingapplication.exception.OtpExpiredException;
import com.jsp.onlineshoppingapplication.exception.RegistrationSessionExpiredException;
import com.jsp.onlineshoppingapplication.exception.UserAlreadyExistException;
import com.jsp.onlineshoppingapplication.mapper.UserMapper;
import com.jsp.onlineshoppingapplication.repo.CustomerRepo;
import com.jsp.onlineshoppingapplication.repo.SellerRepo;
import com.jsp.onlineshoppingapplication.repo.UserRepo;
import com.jsp.onlineshoppingapplication.requestdto.OtpVerficationRequest;
import com.jsp.onlineshoppingapplication.requestdto.UserRequest;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;
import com.jsp.onlineshoppingapplication.service.UserService;
import com.jsp.onlineshoppingapplication.util.ResponseStructure;
import com.jsp.onlineshoppingapplication.util.SimpleStructure;
import com.jsp.onlineshoppingapplication.util.MessageData;
import java.util.*;
import lombok.AllArgsConstructor;
import onlineshoppingapplication.enums.UserRole;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepo userRepo;

	private final CustomerRepo customerRepo;

	private final SellerRepo sellerRepo;

	private final UserMapper userMapper;

	private final Random random;

	private final MailService mailService;


	private final Cache<String, User> userCache;

	private final Cache<String, String> otpCache;

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest, UserRole userRole) {

		User user = null;

		switch (userRole) {
		case CUSTOMER -> user = new Customer();
		case SELLER -> user = new Seller();
		}
		if(user != null) {
			user = userMapper.mapToUser(userRequest, user);
			user.setEmailVerified(false);
			user.setDeleted(false);
		}

		int number = random.nextInt(100000,999999);
		String numberStr = String.valueOf(number);
		userCache.put(user.getEmail(), user);
		otpCache.put(user.getEmail(),numberStr);


		MessageData messageData = new MessageData();

		messageData.setTo(user.getEmail());
		messageData.setSubject("Your OTP Code");
		messageData.setSentDate(new Date());
		messageData.setText(numberStr);

		try {
			mailService.sendMail(messageData);
		} catch(Exception e){
			e.printStackTrace();

		}

		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.ACCEPTED.value())
						.setMessage("Seller Created").setData(userMapper.mapToUserResponse(user)));



	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyUserEmail(OtpVerficationRequest otpVerficationRequest) {


		User user = userCache.getIfPresent(otpVerficationRequest.getEmail());
		String otp = otpCache.getIfPresent(otpVerficationRequest.getEmail());
		
		if(otp == null) throw new OtpExpiredException("Otp Expired");
		if(user == null) throw new  RegistrationSessionExpiredException("Session Expired");
		
		if(!otp.equals(otpVerficationRequest.getOtp())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.UNAUTHORIZED.value())
							.setMessage("Invalid OTP"));
		}
		user.setEmailVerified(true);
		if(user instanceof Seller) {
			((Seller) user).setUserRole(UserRole.SELLER);
			sellerRepo.save((Seller) user);
		}
		else if(user instanceof Customer){
			((Customer)user).setUserRole(UserRole.CUSTOMER);
			customerRepo.save((Customer)user);
			
		}
		userCache.invalidate(otpVerficationRequest.getEmail());
		otpCache.invalidate(otpVerficationRequest.getEmail());
		MessageData messageData = new MessageData();

		messageData.setTo(user.getEmail());
		messageData.setSubject("The registration is done");
		messageData.setSentDate(new Date());
		messageData.setText("your registration is successful for online shopping "+"username:" + user.getUsername());
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.OK.value())
						.setMessage("Email verified successfully")
						.setData(userMapper.mapToUserResponse(user)));

	}

}

