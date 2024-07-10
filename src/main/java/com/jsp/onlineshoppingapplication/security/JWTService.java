package com.jsp.onlineshoppingapplication.security;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jsp.onlineshoppingapplication.entity.RefreshToken;
import com.jsp.onlineshoppingapplication.repo.RefreshTokenRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secret;

    private static final String ROLE = "role";

    private final RefreshTokenRepo refreshTokenRepo;

    public JWTService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public String createJwtToken(String username, long expirationDurationInMillis, String role) {
        return Jwts.builder()
                .setClaims(Map.of(ROLE, role))
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationDurationInMillis))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return parseJwtToken(token).getSubject();
    }

    public Date extractIssuedDate(String token) {
        return parseJwtToken(token).getIssuedAt();
    }

    public Date extractExpireDate(String token) {
        return parseJwtToken(token).getExpiration();
    }

    public String extractUserRole(String token) {
        return parseJwtToken(token).get(ROLE, String.class);
    }

    public boolean isTokenValid(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByRefreshToken(token);
        if (refreshTokenOpt.isEmpty()) {
            return false; // Token not found in repository
        }
        RefreshToken refreshToken = refreshTokenOpt.get();
        return parseJwtToken(token).getExpiration().after(new Date()) && !refreshToken.isBlocked();
    }
}
