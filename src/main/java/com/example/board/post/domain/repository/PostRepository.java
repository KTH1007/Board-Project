package com.example.board.post.domain.repository;

import com.example.board.post.domain.Post;
import com.example.board.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    void deleteByUser(User user);
}
