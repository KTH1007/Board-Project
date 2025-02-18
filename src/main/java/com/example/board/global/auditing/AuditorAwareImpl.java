package com.example.board.global.auditing;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // 현재 사용자를 가져오는 로직이 필요하지만, Security 적용 전이므로 임시 사용자 ID 반환
        return Optional.of(0L);
    }
}
