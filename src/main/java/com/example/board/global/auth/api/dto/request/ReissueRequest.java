package com.example.board.global.auth.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ReissueRequest(
        @NotEmpty
        String refreshToken
) {
}
