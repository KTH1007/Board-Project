package com.example.board.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    // SecretKey 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // AccessToken 생성
    public String createAccessToken(String username, Collection<? extends GrantedAuthority> roles) {
        List<String> roleNames = roles.stream()
                .map(GrantedAuthority::getAuthority) // 권한 이름 추출
                .map(roleName -> roleName.replace("ROLE_", "")) // 접두사 제거
                .toList();
        return createToken(username, roleNames, accessTokenExpiration);
    }

    // RefreshToken 생성
    public String createRefreshToken(String username) {
        return createToken(username, List.of("REFRESH"), refreshTokenExpiration);
    }

    // Token 공통 생성 로직
    private String createToken(String username, List<String> roles, long expiration) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(validity)
                .signWith(getSigningKey()) // 서명
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        log.info("token 값 -> {}", token);
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // 서명 검증
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰에서 role 추출
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // roles 클레임이 null인 경우 빈 리스트 반환
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? roles : List.of();
    }

    // 토큰에서 만료 시간 추출
    public Long getExpirationFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 만료 시을 Date로 추출 후 밀리초 단위로 변환
        Date expiration = claims.getExpiration();
        return expiration != null ? expiration.getTime() : null;
    }


    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
