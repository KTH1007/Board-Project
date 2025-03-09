package com.example.board.user.api.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @Email
        String email,
        @NotEmpty
        String password,
        @NotEmpty @Column(unique = true)
        String nickname,
        @NotNull
        int age
) {
}
