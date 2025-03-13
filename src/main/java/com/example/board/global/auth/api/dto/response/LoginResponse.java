package com.example.board.global.auth.api.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
