package com.jsp.onlineshoppingapplication.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter	
@AllArgsConstructor
@NoArgsConstructor
public class UserNotFoundException extends RuntimeException{

	private String message;
}
