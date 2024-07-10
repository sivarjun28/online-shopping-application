package com.jsp.onlineshoppingapplication.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TokenExpiredException extends RuntimeException {
private String message;
}
