package com.example.board.user.api.dto.response;

public record UpdateUserResponse(
        Long userId,
        String nickname
) {
}
