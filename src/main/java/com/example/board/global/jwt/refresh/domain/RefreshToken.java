package com.example.board.global.jwt.refresh.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshToken_id", updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 사용자 식별자

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Long expiration;

    @Builder
    public RefreshToken(String username, String refreshToken, Long expiration) {
        this.username = username;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    private void updateRefreshToken(String refreshToken, Long expiration) {
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
