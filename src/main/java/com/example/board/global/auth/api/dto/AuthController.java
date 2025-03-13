package com.example.board.global.auth.api.dto;

import com.example.board.global.auth.api.dto.request.LoginRequest;
import com.example.board.global.auth.api.dto.request.ReissueRequest;
import com.example.board.global.auth.api.dto.request.SignUpRequest;
import com.example.board.global.auth.api.dto.response.LoginResponse;
import com.example.board.global.auth.api.dto.response.SignUpResponse;
import com.example.board.global.jwt.TokenProvider;
import com.example.board.global.jwt.refresh.application.RefreshTokenService;
import com.example.board.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "파라미터로 넘어온 정보로 회원가입")
    @ApiResponse(responseCode = "201", description = "회원가입")
    @ApiResponse(responseCode = "400", description = "파리미터 오류")
    @ApiResponse(responseCode = "409", description = "데이터 중복")
    public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "파라미터로 넘어온 정보로 로그인 한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            // SecurityContextHolder에 인증 정보 설정 (JWT 로그인 후 Auditing 사용하기 위해 사용)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증된 사용자 정보 조회
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // role 추출
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // JWT 토큰 생성
            String accessToken = tokenProvider.createAccessToken(userDetails.getUsername(), authorities);
            String refreshToken = tokenProvider.createRefreshToken(userDetails.getUsername());

            // RefreshToken 저장
            refreshTokenService.saveRefreshToken(userDetails.getUsername(), refreshToken);

            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(accessToken, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email이나 password가 유효하지 않습니다.");
        }
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "파라미터로 넘어온 RefreshToken으로 AccessToken을 발급받는다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<LoginResponse> reissueAccessToken(@RequestBody @Valid ReissueRequest reissueRequest) {
        String newAccessToken = refreshTokenService.reissueAccessToken(reissueRequest.refreshToken());
        return ResponseEntity.ok(new LoginResponse(newAccessToken, reissueRequest.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken) {
        refreshToken = refreshToken.substring(7); // "Bearer" 제거

        String username = tokenProvider.getUsernameFromToken(refreshToken);

        // RefreshToken 삭제
        refreshTokenService.deleteRefreshTokenByUsername(username);

        return ResponseEntity.ok("로그아웃");
    }
}
