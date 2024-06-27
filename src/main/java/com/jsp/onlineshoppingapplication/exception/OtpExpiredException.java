package com.jsp.onlineshoppingapplication.exception;

import org.springframework.http.ResponseEntity;

import com.jsp.onlineshoppingapplication.responsedto.UserResponse;
import com.jsp.onlineshoppingapplication.util.ResponseStructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtpExpiredException extends RuntimeException {
	private String message;

}
