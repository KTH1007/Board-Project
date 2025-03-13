package com.example.board.global.jwt.refresh.application;

import com.example.board.global.jwt.TokenProvider;
import com.example.board.global.jwt.refresh.domain.RefreshToken;
import com.example.board.global.jwt.refresh.domain.RefreshTokenRepository;
import com.example.board.global.security.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final UserDetailService userDetailService;

    // RefreshToken 저장
    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .username(username)
                .refreshToken(refreshToken)
                .expiration(Instant.now().toEpochMilli() + tokenProvider.getRefreshTokenExpiration())
                .build();

        refreshTokenRepository.save(token);
    }

    // RefreshToken 조회
    public RefreshToken findRefreshTokenUsername(String username) {
        return getRefreshToken(username);
    }

    // RefreshToken 삭제
    @Transactional
    public void deleteRefreshTokenByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }

    // RefreshToken 유효성 검사 및 AccessToken 재발급
    @Transactional
    public String reissueAccessToken(String refreshToken) {
        // RefreshToken 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        // RefreshToken에서 사용자 이름 추출
        String username = tokenProvider.getUsernameFromToken(refreshToken);

        // DB에서 RefreshToken 조회
        RefreshToken storedToken = getRefreshToken(username);

        // 저장된 RefreshToken과 요청된 RefreshToken 비교
        if (!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("RefreshToken mismatch");
        }

        // RefreshToken 만료 확인
        if (storedToken.getExpiration() < Instant.now().toEpochMilli()) {
            throw new RuntimeException("RefreshToken expired");
        }

        // role 가져오기
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // 새로운 AccessToken 발급
        return tokenProvider.createAccessToken(username, authorities);
    }

    // 만료된 RefreshToken 삭제
    @Scheduled(fixedRate = 86400000) // 24시간마다 실행
    @Transactional
    public void deleteExpiredRefreshTokens() {
        refreshTokenRepository.deleteAllByExpirationLessThan(Instant.now().toEpochMilli());
    }


    private RefreshToken getRefreshToken(String username) {
        return refreshTokenRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Not Found Refresh Token"));
    }
}
