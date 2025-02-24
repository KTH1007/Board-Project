package com.example.board.comment.domain.repository;

import com.example.board.comment.api.dto.response.CommentResponse;
import com.example.board.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {
    Page<CommentResponse> findByPostIdWithReplies(Long postId, Pageable pageable);

    List<Comment> findByPostIdWithReplies(Long postId);
}
