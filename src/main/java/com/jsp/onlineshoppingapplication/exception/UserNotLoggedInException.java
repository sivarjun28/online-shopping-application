package com.jsp.onlineshoppingapplication.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter	
@Getter
public class UserNotLoggedInException extends RuntimeException {
private String message;
}
