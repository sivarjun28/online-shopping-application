package com.jsp.onlineshoppingapplication.securityfilters;



import com.jsp.onlineshoppingapplication.repo.AccessTokenRepo;
import com.jsp.onlineshoppingapplication.repo.RefreshTokenRepo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenValidation {
	
	private final AccessTokenRepo accessTokenRepository;
	private final RefreshTokenRepo refreshTokenRepository;

}
