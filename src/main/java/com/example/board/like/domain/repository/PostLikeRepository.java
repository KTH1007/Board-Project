package com.example.board.like.domain.repository;

import com.example.board.like.domain.PostLike;
import com.example.board.post.domain.Post;
import com.example.board.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post); // 중복 추천 방지

    Optional<PostLike> findByUserAndPost(User user, Post post);
}
