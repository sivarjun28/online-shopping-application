package com.jsp.onlineshoppingapplication.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter	
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationSessionExpiredException extends RuntimeException {

	private String message;
}
