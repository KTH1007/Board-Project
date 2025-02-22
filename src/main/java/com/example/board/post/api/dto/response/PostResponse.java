package com.example.board.post.api.dto.response;

import com.example.board.post.domain.Category;
import com.example.board.post.domain.Post;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponse(
        Long id,
        @NotNull
        String title,
        @NotNull
        String content,
        Category category,
        int likeCount,
        Long createdId,
        Long updatedId,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {

    public static PostResponse toDto(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .likeCount(post.getLikeCount())
                .createdId(post.getCreatedId())
                .updatedId(post.getUpdatedId())
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .build();
    }
}
