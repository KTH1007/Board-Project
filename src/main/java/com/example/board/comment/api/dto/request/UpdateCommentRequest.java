package com.example.board.comment.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCommentRequest(
        @NotNull
        String content
) {
}
