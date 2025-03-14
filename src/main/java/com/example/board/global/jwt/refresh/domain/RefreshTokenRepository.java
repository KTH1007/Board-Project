package com.example.board.global.jwt.refresh.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUsername(String username);

    void deleteByUsername(String username);

    void deleteAllByExpirationLessThan(long epochMilli);
}
