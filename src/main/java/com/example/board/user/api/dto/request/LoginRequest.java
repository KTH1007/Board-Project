package com.example.board.user.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @Email
        String email,
        @NotEmpty
        String password
) {
}
