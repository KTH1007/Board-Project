package com.example.board.like.application;

import com.example.board.like.domain.PostLike;
import com.example.board.like.domain.repository.PostLikeRepository;
import com.example.board.like.exception.LikeNotFoundException;
import com.example.board.post.domain.Post;
import com.example.board.post.domain.repository.PostRepository;
import com.example.board.post.exception.NotFoundPostException;
import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import com.example.board.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void likePost(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);
        // 중복 추천 방지
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new RuntimeException("이미 추천을 눌렀습니다.");
        }

        PostLike postLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        postLikeRepository.save(postLike);
        post.increaseLikeCount();
    }

    public void unlikePost(Long userId, Long postId) {
        User user = getUser(userId);
        Post post = getPost(postId);

        // 추천하지 않은 경우 예외 발생
        PostLike postLike = postLikeRepository.findByUserAndPost(user, post).orElseThrow(LikeNotFoundException::new);
        postLikeRepository.delete(postLike);
        post.decreaseLikeCount();
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundUserException::new);
    }

    private Post getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundPostException::new);
        return post;
    }
}
