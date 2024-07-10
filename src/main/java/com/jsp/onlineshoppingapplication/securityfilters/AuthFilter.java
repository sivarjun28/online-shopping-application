package com.jsp.onlineshoppingapplication.securityfilters;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.onlineshoppingapplication.security.JWTService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
	
	private final JWTService jwtService;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		    String token = null;

		    if (request.getCookies() != null) {
		        for (Cookie cookie : request.getCookies()) {
		        	System.out.println(cookie.getName());
		            if ("at".equals(cookie.getName())) {
		                token = cookie.getValue();
		                break;  // Exit the loop once token is found
		            }
		        }
		    }
		    
		    System.out.println(token);

		    if (token != null) {
		    	try {
		    		if (SecurityContextHolder.getContext().getAuthentication() == null) {
		    			String username = jwtService.extractUsername(token);
		    			String role = jwtService.extractUserRole(token);
		    			
		    			if (username == null || role == null) {
		    				throw new IllegalArgumentException("Username or role value is null");
		    			}
		    			Collection<? extends GrantedAuthority> userRole = Collections.singletonList(new SimpleGrantedAuthority(role));
		    	
		    			UsernamePasswordAuthenticationToken usernameAuthenticationToken =
		    					new UsernamePasswordAuthenticationToken(username, null, userRole);
		    			usernameAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
		    			
		    			SecurityContextHolder.getContext().setAuthentication(usernameAuthenticationToken);
		    		}
		    	} 
		    	catch (ExpiredJwtException e) {
		    	   
		    	 TokenExceptionHandler.tokenHandler(HttpStatus.UNAUTHORIZED.value(), "Failed to Authenticate",
		    			 "The token is already expired", response);
		    	 return;
		    	 
		    	} 
		    	
		    	
		    	catch (JwtException e) {
		    		System.out.println("Invalid JWT token");
		    		 TokenExceptionHandler.tokenHandler(HttpStatus.UNAUTHORIZED.value(), "Failed to Authenticate",
			    			 "The token is already expired", response);
		    		//throw new InvalidJwtException("Exiped jwt");
		    	}
		    }

		    

		    filterChain.doFilter(request, response);

		    //signature not correct
		    //jwt expired futher we have to connect with client for re-login
		    //
		    
		    
		 
	}

}
