package com.jsp.onlineshoppingapplication.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AccessToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tokenId;
	private String token;
	private LocalDateTime expiration;
	private boolean isBlocked;
	
	@ManyToOne
	private User user;
}
