package com.example.board.like.domain.repository;

import com.example.board.comment.domain.Comment;
import com.example.board.like.domain.CommentLike;
import com.example.board.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment);

    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}
