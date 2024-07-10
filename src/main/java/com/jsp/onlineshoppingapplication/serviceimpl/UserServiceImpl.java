package com.jsp.onlineshoppingapplication.serviceimpl;


import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.jsp.onlineshoppingapplication.entity.AccessToken;
import com.jsp.onlineshoppingapplication.entity.Customer;
import com.jsp.onlineshoppingapplication.entity.RefreshToken;
import com.jsp.onlineshoppingapplication.entity.Seller;
import com.jsp.onlineshoppingapplication.entity.User;
import com.jsp.onlineshoppingapplication.exception.InvalidTokenException;
import com.jsp.onlineshoppingapplication.exception.OtpExpiredException;
import com.jsp.onlineshoppingapplication.exception.RegistrationSessionExpiredException;
import com.jsp.onlineshoppingapplication.exception.TokenExpiredException;
import com.jsp.onlineshoppingapplication.exception.UserNotFoundException;
import com.jsp.onlineshoppingapplication.exception.UserNotLoggedInException;
import com.jsp.onlineshoppingapplication.exception.UsernameNotFoundException;
import com.jsp.onlineshoppingapplication.mapper.UserMapper;
import com.jsp.onlineshoppingapplication.repo.AccessTokenRepo;
import com.jsp.onlineshoppingapplication.repo.CustomerRepo;
import com.jsp.onlineshoppingapplication.repo.RefreshTokenRepo;
import com.jsp.onlineshoppingapplication.repo.SellerRepo;
import com.jsp.onlineshoppingapplication.repo.UserRepo;
import com.jsp.onlineshoppingapplication.requestdto.AuthRequest;
import com.jsp.onlineshoppingapplication.requestdto.OtpVerficationRequest;
import com.jsp.onlineshoppingapplication.requestdto.UserRequest;
import com.jsp.onlineshoppingapplication.responsedto.AuthResponse;
import com.jsp.onlineshoppingapplication.responsedto.UserResponse;
import com.jsp.onlineshoppingapplication.security.JWTService;
import com.jsp.onlineshoppingapplication.service.UserService;
import com.jsp.onlineshoppingapplication.util.ResponseStructure;
import com.jsp.onlineshoppingapplication.util.SimpleStructure;
import com.jsp.onlineshoppingapplication.util.MessageData;

import java.time.LocalDateTime;
import java.util.*;
import lombok.AllArgsConstructor;
import onlineshoppingapplication.enums.UserRole;

@Service
public class UserServiceImpl implements UserService{

	private final UserRepo userRepo;
	private final CustomerRepo customerRepo;
	private final SellerRepo sellerRepo;
	private final AccessTokenRepo accessTokenRepo;
	private final RefreshTokenRepo refreshTokenRepo;
	private final UserMapper userMapper;
	private final Random random;
	private final MailService mailService;
	private JWTService jwtService;
	private final Cache<String, User> userCache;
	private final Cache<String, String> otpCache;
	private final AuthenticationManager authenticationManager;

	/**Whenever we use @Value we should remove @AllArgsConstructor and generate our own constructors with fields
	 * which are not annotated over @Value for Constructor Injection*/

	@Value("${application.jwt.access_expiry_seconds}")
	private long accessExpirySeconds;

	@Value("${application.jwt.refresh_expiry_seconds}")
	private long refreshExpirySeconds;

	@Value("${application.cookie.domain}")
	private String domain;

	@Value("${application.cookie.same_site}")
	private String sameSite;

	@Value("${application.cookie.secure}")
	private boolean secure;



	public UserServiceImpl(UserRepo userRepo, CustomerRepo customerRepo,
			SellerRepo sellerRepo, UserMapper userMapper, Cache<String, User> userCache,
			Cache<String, String> otpCache, Random random, MailService mailService,
			AuthenticationManager authenticationManager, JWTService jwtService,
			AccessTokenRepo accessTokenRepo, RefreshTokenRepo refreshTokenRepo) {
		this.userRepo = userRepo;
		this.customerRepo = customerRepo;
		this.sellerRepo = sellerRepo;
		this.userMapper = userMapper;
		this.userCache = userCache;
		this.otpCache = otpCache;
		this.random = random;
		this.mailService = mailService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest, UserRole userRole) {

	    User user = switch (userRole) {
	        case CUSTOMER -> new Customer();
	        case SELLER -> new Seller();
	    };

	    if (user != null) {
	        user = userMapper.mapToUser(userRequest, user);
	        user.setEmailVerified(false);
	        user.setDeleted(false);
	    }

	    int number = random.nextInt(100000, 999999);
	    String numberStr = String.valueOf(number);
	    userCache.put(user.getEmail(), user);
	    otpCache.put(user.getEmail(), numberStr);

	    MessageData messageData = new MessageData();
	    messageData.setTo(user.getEmail());
	    messageData.setSubject("Your OTP Code");
	    messageData.setSentDate(new Date());
	    messageData.setText(numberStr);

	    try {
	        mailService.sendMail(messageData);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ResponseEntity.status(HttpStatus.ACCEPTED)
	            .body(new ResponseStructure<UserResponse>()
	                    .setStatus(HttpStatus.ACCEPTED.value())
	                    .setMessage("Seller Created")
	                    .setData(userMapper.mapToUserResponse(user)));
	}


	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyUserEmail(OtpVerficationRequest otpVerficationRequest) {


		User user = userCache.getIfPresent(otpVerficationRequest.getEmail());
		String otp = otpCache.getIfPresent(otpVerficationRequest.getEmail());

		String email = user.getEmail();
		if(email == null) {
			throw new IllegalArgumentException("Invalid email");
		}
		int atIndex = email.indexOf("@");
		String name = email.substring(0,atIndex);
		if(otp == null) throw new OtpExpiredException("Otp Expired");
		if(user == null) throw new  RegistrationSessionExpiredException("Session Expired");

		if(!otp.equals(otpVerficationRequest.getOtp())) {


			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.UNAUTHORIZED.value())
							.setMessage("Invalid OTP"));
		}
		user.setUsername(name);
		user.setEmailVerified(true);
		if(user instanceof Seller) {
			((Seller) user).setUserRole(UserRole.SELLER);
			sellerRepo.save((Seller) user);
		}
		else if(user instanceof Customer){
			((Customer)user).setUserRole(UserRole.CUSTOMER);
			customerRepo.save((Customer)user);

		}

		MessageData messageData = new MessageData();

		messageData.setTo(user.getEmail());
		messageData.setSubject("The registration is done");
		messageData.setSentDate(new Date());
		messageData.setText("your registration is successful for online shopping "+"username:" + user.getUsername());
		try {
			mailService.sendMail(messageData);
		} catch(Exception e){
			e.printStackTrace();

		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.OK.value())
						.setMessage("Email verified successfully")
						.setData(userMapper.mapToUserResponse(user)));

	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> jwtLogin(AuthRequest authRequest) {
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword() ));

		if (authenticate.isAuthenticated()) {

			return userRepo.findByUsername(authRequest.getUsername()).map(user ->{
				HttpHeaders httpHeaders = new HttpHeaders();
				grantAccessToken(httpHeaders, user);
				grantRefreshToken(httpHeaders, user);

				return ResponseEntity.ok().headers(httpHeaders)
						.body(new ResponseStructure<AuthResponse>()
								.setStatus(HttpStatus.OK.value())
								.setMessage("Login successful")
								.setData(AuthResponse.builder()
										.userId(user.getUserId())
										.username(user.getUsername())
										.role(user.getUserRole().name())
										.accessExpiration(accessExpirySeconds)
										.refreshExpiration(refreshExpirySeconds)
										.build()));
			}).orElseThrow(()-> new UsernameNotFoundException("User not found"));

		} else {

			throw new BadCredentialsException("BadCredentials");
		}
	}


	
	private void grantAccessToken(HttpHeaders httpHeaders, User user) {

		String jwtToken = jwtService.createJwtToken(user.getUsername(), 3600000L, user.getUserRole().toString()); //1hr in milliseconds
		AccessToken accessToken = new AccessToken();
		accessToken.setToken(jwtToken);
		accessToken.setExpiration(LocalDateTime.now().plusSeconds(3600)); //3600000%1000 
		accessToken.setUser(user);
		accessTokenRepo.save(accessToken);

		httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("at", jwtToken, accessExpirySeconds)); //at -> access token

	}

	private void grantRefreshToken(HttpHeaders httpHeaders, User user) {

		String jwtToken = jwtService.createJwtToken(user.getUsername(), (1000*60*60*24*15L), user.getUserRole().toString()); //1hr in milliseconds
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setRefreshToken(jwtToken);
		refreshToken.setExpiration(LocalDateTime.now().plusSeconds(3600)); //3600000%1000 
		refreshToken.setUser(user);
		refreshTokenRepo.save(refreshToken);

		httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("rt", jwtToken, refreshExpirySeconds)); //rt -> refresh token

	}

	private String generateCookie(String name, String value, long maxAge) {
		return ResponseCookie.from(name, value) 
				.domain(domain)
				.path("/")
				.maxAge(maxAge)
				.sameSite(sameSite) //who can issue the cookie to browser
				.httpOnly(true) //to access only by server not client
				.secure(secure)
				.build()
				.toString();
	}
	  public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String refreshToken) {
	        Date expirationDate = jwtService.extractExpireDate(refreshToken);

	        if (expirationDate.getTime() < new Date().getTime()) {
	            throw new TokenExpiredException("Refresh token has expired. Please make a new SignIn request.");
	        }

	        String username = jwtService.extractUsername(refreshToken);
	        String userRole = jwtService.extractUserRole(refreshToken);

	        User user = userRepo.findByUsername(username)
	                            .orElseThrow(() -> new UserNotFoundException("User not found for username: " + username));

	        // Clean up expired access tokens
	        List<AccessToken> expiredTokens = accessTokenRepo.findAllByExpirationBefore(LocalDateTime.now());
	        accessTokenRepo.deleteAll(expiredTokens);

	        // Grant a new access token
	        HttpHeaders httpHeaders = new HttpHeaders();
	        grantAccessToken(httpHeaders, user);

	        // Calculate refresh token remaining validity
	        long refreshExpiryRemaining = expirationDate.getTime() - new Date().getTime();

	        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders)
	                             .body(new ResponseStructure<AuthResponse>()
	                                         .setStatus(HttpStatus.OK.value())
	                                         .setMessage("Access Token renewed")
	                                         .setData(AuthResponse.builder()
	                                                              .userId(user.getUserId())
	                                                              .username(user.getUsername())
	                                                              .role(userRole)
	                                                              .accessExpiration(accessExpirySeconds)
	                                                              .refreshExpiration(refreshExpiryRemaining)
	                                                              .build()));
	    }
		



	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(String refreshToken, String accessToken) {

		if (refreshToken == null || accessToken == null)
			throw new UserNotLoggedInException("Please login first");
		else {
			Optional<RefreshToken> optionalRefreshToken = refreshTokenRepo.findByRefreshToken(refreshToken);
			Optional<AccessToken> optionalAccessToken = accessTokenRepo.findByToken(accessToken);
			RefreshToken existRefreshToken = optionalRefreshToken.get();
			AccessToken existAccessToken = optionalAccessToken.get();

			existRefreshToken.setBlocked(true);
			existAccessToken.setBlocked(true);
			refreshTokenRepo.save(existRefreshToken);
			accessTokenRepo.save(existAccessToken);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("rt", null, 0));
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("at", null, 0));

			User user = existRefreshToken.getUser();
			return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders)
					.body(new ResponseStructure<AuthResponse>().setStatus(HttpStatus.OK.value())
							.setMessage("User logout done")
							.setData(AuthResponse.builder().userId(user.getUserId()).username(user.getUsername())
									.role(user.getUserRole().toString()).accessExpiration(0).refreshExpiration(0)
									.build()));
			
		}
	}

	@Override
	public ResponseEntity<SimpleStructure> logoutFromOtherDevices(String refreshToken, String accessToken) {
		// TODO Auto-generated method stub
		String username = jwtService.extractUsername(refreshToken);
		return userRepo.findByUsername(username).map(user -> {

			accessTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false,accessToken).forEach(at -> {
				at.setBlocked(true);
				accessTokenRepo.save(at);
			});

			refreshTokenRepo.findByUserAndIsBlockedAndRefreshTokenNot(user, false,refreshToken).forEach(rt -> {
				rt.setBlocked(true);
				refreshTokenRepo.save(rt);
			});

		

			return ResponseEntity.status(HttpStatus.OK).body(new SimpleStructure()
					.setStatus(HttpStatus.OK.value()).setMessage("logout from all other devices Successfully"));
		}).orElseThrow(() -> new UsernameNotFoundException("failed to logout from other devices"));

	}

	@Override
	public ResponseEntity<SimpleStructure> logoutFromAllDevices(String accessToken) {
		// TODO Auto-generated method stub

		String username = jwtService.extractUsername(accessToken);

		return userRepo.findByUsername(username).map(user -> {

			accessTokenRepo.findByUserAndIsBlocked(user, false).forEach(at -> {
				at.setBlocked(true);
				accessTokenRepo.save(at);
			});

			refreshTokenRepo.findByUserAndIsBlocked(user, false).forEach(rt -> {
				rt.setBlocked(true);
				refreshTokenRepo.save(rt);
			});

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("at", null, 0));
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("rt", null, 0));

			return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(new SimpleStructure()
					.setStatus(HttpStatus.OK.value()).setMessage("logout from all devices Successfully"));
		}).orElseThrow(() -> new UsernameNotFoundException("failed to logout"));

	}

}

