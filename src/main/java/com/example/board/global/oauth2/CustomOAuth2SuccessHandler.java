package com.example.board.global.oauth2;

import com.example.board.global.auth.api.dto.response.LoginResponse;
import com.example.board.global.jwt.TokenProvider;
import com.example.board.global.jwt.refresh.application.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2User에서 사용자 정보 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities(); // 권한 정보 추출

        // JWT 토큰 생성
        String accessToken = tokenProvider.createAccessToken(email, authorities);
        String refreshToken = tokenProvider.createRefreshToken(email);

        // RefreshToken 저장
        refreshTokenService.saveRefreshToken(email, refreshToken);

        // 토큰을 DTO로 변환하여 JSON 반환
        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(loginResponse));
    }
}
