package com.example.board.comment.domain.repository;

import com.example.board.comment.domain.Comment;
import com.example.board.post.domain.Post;
import com.example.board.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByUser(User user);

    void deleteByPost(Post post);
}
