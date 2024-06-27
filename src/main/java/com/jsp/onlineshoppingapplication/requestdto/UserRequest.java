package com.jsp.onlineshoppingapplication.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onlineshoppingapplication.enums.UserRole;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	
	@NotNull(message = "username cannot be null")
	@NotBlank(message = "username cannot be blank")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name should only contain alphabetic characters")
	private String username;
	
	@Email(regexp = "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	private String email;
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must"
			+ " contain at least one letter, one number, one special character")
	private String password;
	UserRole userRole;

}
