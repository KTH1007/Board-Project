package com.example.board.post.api.dto.request;

import com.example.board.post.domain.Category;
import jakarta.validation.constraints.NotNull;

public record UpdatePostRequest(
        @NotNull
        String title,
        @NotNull
        String content,
        Category category
) {
}
