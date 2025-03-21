package com.example.board.global.security.config;

import com.example.board.global.jwt.JwtAuthenticationFilter;
import com.example.board.global.jwt.TokenProvider;
import com.example.board.global.oauth2.CustomOAuth2SuccessHandler;
import com.example.board.global.oauth2.CustomOAuth2UserService;
import com.example.board.global.security.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final EncoderConfig encoderConfig;
    private final TokenProvider tokenProvider;
    private final UserDetailService userDetailService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/login", "/signup").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
//                .formLogin(form -> form
//                        .defaultSuccessUrl("/") // 로그인 성공 시 이동할 페이지
//                        .failureUrl("/login?error=true") // 로그인 실패 시 이동할 페이지
//                        .usernameParameter("email") // 사용자명 파라미터 (기본값은 "username")
//                        .passwordParameter("password") // 비밀번호 파라미터 (기본값은 "password")
//                        .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/login")) // 인가되지 않은 페이지 접속 시 리다이렉트
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler))
                .logout(logout -> logout.disable()) // 기본 로그아웃 비활성화 (직접 로그아웃 구현 시 충돌)
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true) // 로그아웃 이후 세션 전체 삭제
//                        .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 삭제
//                        .clearAuthentication(true)) // 로그아웃 시 인증 정보 삭제
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
