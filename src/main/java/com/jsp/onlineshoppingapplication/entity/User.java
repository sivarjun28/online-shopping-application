package com.jsp.onlineshoppingapplication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onlineshoppingapplication.enums.UserRole;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int userId;
private String username;
private String email;
private String password;
@Enumerated(EnumType.STRING)
UserRole userRole;
boolean isEmailVerified;
boolean isDeleted;
}
