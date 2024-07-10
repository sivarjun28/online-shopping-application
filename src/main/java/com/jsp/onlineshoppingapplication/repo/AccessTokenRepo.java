package com.jsp.onlineshoppingapplication.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;

import com.jsp.onlineshoppingapplication.entity.AccessToken;
import com.jsp.onlineshoppingapplication.entity.User;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer>{

	Optional<AccessToken> findByToken(String accessToken);

	List<AccessToken> findByUserAndIsBlocked(User user, boolean b);

	List<AccessToken> findByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	List<AccessToken> findAllByExpirationBefore(LocalDateTime now);


}
