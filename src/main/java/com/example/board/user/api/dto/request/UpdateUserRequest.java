package com.example.board.user.api.dto.request;

public record UpdateUserRequest(
        String nickname,
        String oldPassword,
        String newPassword
) {
}
