package com.example.board.like.application;

import com.example.board.comment.domain.Comment;
import com.example.board.comment.domain.repository.CommentRepository;
import com.example.board.comment.exception.NotFoundCommentException;
import com.example.board.like.domain.CommentLike;
import com.example.board.like.domain.repository.CommentLikeRepository;
import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import com.example.board.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentLikeService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public void likeComment(Long postId, Long userId, Long commentId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        commentContainsPost(postId, comment);

        // 중복 추천 방지
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            throw new RuntimeException("이미 추천을 눌렀습니다.");
        }

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
        comment.increaseLikeCount();
        commentLikeRepository.save(commentLike);
    }

    @Transactional
    public void unlikeComment(Long postId, Long userId, Long commentId) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        commentContainsPost(postId, comment);

        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment).orElseThrow(() ->
                new RuntimeException("해당 유저가 추천한 댓글을 찾을 수 없습니다."));
        commentLikeRepository.delete(commentLike);
        comment.decreaseLikeCount();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundUserException::new);
    }

    private static void commentContainsPost(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("댓글이 해당 게시글에 포함되어 있지 않습니다.");
        }
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NotFoundCommentException::new);
    }
}
