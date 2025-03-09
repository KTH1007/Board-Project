package com.example.board.comment.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotNull
        String content,
        Long parentCommentId
) {
}
