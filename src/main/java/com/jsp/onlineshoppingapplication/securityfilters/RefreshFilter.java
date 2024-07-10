package com.jsp.onlineshoppingapplication.securityfilters;

import com.jsp.onlineshoppingapplication.exception.UsernameRoleNotFoundException;
import com.jsp.onlineshoppingapplication.security.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class RefreshFilter extends OncePerRequestFilter {

	private JWTService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		TokenExceptionHandler handler = new TokenExceptionHandler();
		Cookie[] cookie = request.getCookies();
		String refreshToken = null;

		if (cookie != null) {

			for (Cookie cookies : cookie) {

				if (cookies.getName().equals("rt")) {

					refreshToken = cookies.getValue();
				}

				if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
					handler.tokenHandler(HttpStatus.UNAUTHORIZED.value(), "Token is Blocked", "Token is not available", response);
					return;
				}

				try {
					String username = jwtService.extractUsername(refreshToken);
					String userRole = jwtService.extractUserRole(refreshToken);

					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								username, null, List.of(new SimpleGrantedAuthority(userRole.toString())));
						usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					}

				} catch ( Exception e) {

					handler.tokenHandler(HttpStatus.UNAUTHORIZED.value(), "Token is Blocked", "Token is not available", response);
				}

			}
			filterChain.doFilter(request, response);
        }
    }
}
