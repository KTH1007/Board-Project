package com.example.board.global.auth.api.dto.response;

import com.example.board.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignUpResponse(
        String email,
        String nickname,
        int age,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {

    public static SignUpResponse toDto(User user) {
        return SignUpResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .age(user.getAge())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .build();
    }
}
