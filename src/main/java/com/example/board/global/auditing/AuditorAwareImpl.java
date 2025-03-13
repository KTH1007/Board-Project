package com.example.board.global.auditing;

import com.example.board.global.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 현재 로그인한 사용자의 정보를 가져온다.
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty(); // 인증되지 않은 경우
        }

        // 사용자 ID 추출
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return Optional.of(userDetails.getUserId()); // 사용자 ID 반환
        }

        return Optional.empty(); // 사용자 정보가 없는 경우
    }
}
