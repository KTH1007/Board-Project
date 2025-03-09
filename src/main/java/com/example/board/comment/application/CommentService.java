package com.example.board.comment.application;

import com.example.board.comment.api.dto.request.CreateCommentRequest;
import com.example.board.comment.api.dto.request.UpdateCommentRequest;
import com.example.board.comment.api.dto.response.CommentResponse;
import com.example.board.comment.domain.Comment;
import com.example.board.comment.domain.repository.CommentRepository;
import com.example.board.comment.domain.repository.CommentRepositoryCustom;
import com.example.board.comment.exception.NotFoundCommentException;
import com.example.board.comment.exception.ParentCommentNotFoundException;
import com.example.board.post.domain.Post;
import com.example.board.post.domain.repository.PostRepository;
import com.example.board.post.exception.NotFoundPostException;
import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import com.example.board.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepositoryCustom commentRepositoryCustom;

    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest createCommentRequest) {
        Post post = getPost(postId);
        User user = getCurrentUser();

        Comment parentComment = null;
        log.info("request -> {}", createCommentRequest.parentCommentId());
        if (createCommentRequest.parentCommentId() != null) {
            // 대댓글인 경우, 부모 댓글 조회
            parentComment = getParentComment(createCommentRequest);
            log.info("parentComment -> {}", parentComment);
        }

        Comment comment = Comment.builder()
                .content(createCommentRequest.content())
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .build();

        commentRepository.save(comment);

        return CommentResponse.toDto(comment);
    }

    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepositoryCustom.findByPostIdWithReplies(postId, pageable);
    }

    public List<CommentResponse> findAll(Long postId) {
        // 해당 게시글에 달린 댓글 조회 (대댓글 포함)
        List<Comment> comments = commentRepositoryCustom.findByPostIdWithReplies(postId);

        // 부모 댓글만 필터링
        Map<Long, CommentResponse> commentMap = new HashMap<>();
        List<CommentResponse> result = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse commentResponse = CommentResponse.toDto(comment);
            if (comment.getParentComment() == null) {
                // 부모 댓글
                result.add(commentResponse);
            } else {
                // 대댓글인 경우, 부모 댓글의 자식으로 추가
                CommentResponse parentResponse = commentMap.get(comment.getParentComment().getId());
                if (parentResponse != null) {
                    parentResponse.childComments().add(commentResponse);
                }
            }
            commentMap.put(comment.getId(), commentResponse);
        }

        return result;
    }

    @Transactional
    public CommentResponse updateComment(Long postId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = getComment(commentId);

        // 댓글이 해당 게시글에 속하는지 확인
        commentContainsPost(postId, comment);

        comment.updateContent(updateCommentRequest.content());
        commentRepository.save(comment);
        return CommentResponse.toDto(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = getComment(commentId);

        // 댓글이 해당 게시글에 속하는지 확인
        commentContainsPost(postId, comment);

        commentRepository.delete(comment);
    }

    private static void commentContainsPost(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("댓글이 해당 게시글에 포함되어 있지 않습니다.");
        }
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NotFoundCommentException::new);
    }


    private Comment getParentComment(CreateCommentRequest createCommentRequest) {
        return commentRepository.findById(createCommentRequest.parentCommentId()).orElseThrow(ParentCommentNotFoundException::new);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username).orElseThrow(NotFoundUserException::new);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(NotFoundPostException::new);
    }
}
