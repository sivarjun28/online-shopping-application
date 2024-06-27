package com.jsp.onlineshoppingapplication.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class SimpleStructure<T> {
	
private int status;
private String message;

public String getMessage() {
	return message;
}

public SimpleStructure<T> setMessage(String message) {
	this.message = message;
	return this;
	
}

public int getStatus() {
	return status;
}

public SimpleStructure<T> setStatus(int status) {
	this.status = status;
	return this;
}

}
