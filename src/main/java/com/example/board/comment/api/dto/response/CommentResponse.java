package com.example.board.comment.api.dto.response;

import com.example.board.comment.domain.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CommentResponse(
        Long id,
        @NotNull
        String content,
        Long userId,
        Long postId,
        int likeCount,
        Long createdId,
        Long updatedId,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,
        List<CommentResponse> childComments // 대댓글 목록
) {
    public static CommentResponse toDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .likeCount(comment.getLikeCount())
                .createdId(comment.getCreatedId())
                .updatedId(comment.getUpdatedId())
                .createdDate(comment.getCreatedDate())
                .updatedDate(comment.getUpdatedDate())
                // 자식 댓글이 null이 아닌 경우에만 가져옴
                .childComments(comment.getChildComments() == null ? List.of() : comment.getChildComments().stream()
                        .map(CommentResponse::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
