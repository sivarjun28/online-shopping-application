package com.jsp.onlineshoppingapplication.requestdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerficationRequest {
	
	private String email;
	private String otp;
	
}
