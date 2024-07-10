package com.jsp.onlineshoppingapplication.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.onlineshoppingapplication.entity.RefreshToken;
import com.jsp.onlineshoppingapplication.entity.User;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findByUserAndIsBlocked(User user, boolean b);

    List<RefreshToken> findByUserAndIsBlockedAndRefreshTokenNot(User user, boolean b, String refreshToken);

    List<RefreshToken> findAllByExpirationBefore(LocalDateTime now);
}
