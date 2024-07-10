package com.jsp.onlineshoppingapplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jsp.onlineshoppingapplication.securityfilters.JwtAuthFilter;
import com.jsp.onlineshoppingapplication.securityfilters.LoginFilter;
import com.jsp.onlineshoppingapplication.securityfilters.RefreshFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
	
	private final JWTService jwtService;
	
	@Bean
	PasswordEncoder  passwordEncoder() {
		return new BCryptPasswordEncoder(12);  

	}

 
    
    @Bean
    @Order(1)
    SecurityFilterChain loginSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable())
        		.securityMatchers(matcher -> matcher.requestMatchers("/api/v1/login/**", "/api/v1/register/seller/**", "/api/v1/register/customer/**", "/api/v1/users/otp/**"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new LoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    
    @Bean
    @Order(2)
    SecurityFilterChain refreshFilterChain(HttpSecurity httpSecurity) throws Exception
    {
    	return httpSecurity.csrf(csrf -> csrf.disable())
    			.securityMatchers(match -> match.requestMatchers("/api/v1/refresh-login/**"))
    			.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
    			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    			.addFilterBefore(new RefreshFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
    			.build();
    }
    
	@Bean
	@Order(3)
	SecurityFilterChain securityFilterChain( HttpSecurity security) throws Exception {
		return security.csrf(csrf -> csrf.disable())
				.securityMatchers(match -> match.requestMatchers("/api/v1/**"))
				.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	@Bean
	AuthenticationManager getAuthentication(AuthenticationConfiguration authenticationConfiguration) throws Exception
	{
		return authenticationConfiguration.getAuthenticationManager();
	}
	
}
