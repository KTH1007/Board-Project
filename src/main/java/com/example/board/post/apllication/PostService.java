package com.example.board.post.apllication;

import com.example.board.comment.domain.repository.CommentRepository;
import com.example.board.post.api.dto.request.CreatePostRequest;
import com.example.board.post.api.dto.request.SearchPostRequest;
import com.example.board.post.api.dto.request.UpdatePostRequest;
import com.example.board.post.api.dto.response.PostResponse;
import com.example.board.post.domain.Post;
import com.example.board.post.domain.repository.PostRepository;
import com.example.board.post.domain.repository.PostRepositoryCustom;
import com.example.board.post.exception.NotFoundPostException;
import com.example.board.user.domain.User;
import com.example.board.user.domain.UserRepository;
import com.example.board.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponse createPost(CreatePostRequest postRequest, Long currentUserId) {
        // 현재 로그인한 사용자의 정보 가져오기
        User user = getUser(currentUserId);

        Post post = Post.builder()
                .title(postRequest.title())
                .content(postRequest.content())
                .category(postRequest.category())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.toDto(savedPost);
    }

    public List<PostResponse> findAll() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostResponse::toDto)
                .collect(Collectors.toList());
    }

    public Page<PostResponse> searchAndPagePost(SearchPostRequest searchPostRequest, Pageable pageable) {
        return postRepositoryCustom.searchPage(searchPostRequest, pageable);
    }

    public PostResponse findPost(Long id) {
        return PostResponse.toDto(getPost(id));
    }

    @Transactional
    public PostResponse updatePost(Long id, UpdatePostRequest updatePostRequest, Long currentUserId) throws AccessDeniedException {
        Post post = getPost(id);

        // 작성자와 현재 로그인한 사용자가 같은지 확인
        currentUserCheck(post, currentUserId);

        post.updatePost(updatePostRequest.title(), updatePostRequest.content(), updatePostRequest.category());
        return PostResponse.toDto(post);
    }

    @Transactional
    public void deletePost(Long id, Long currentUserId) throws AccessDeniedException {
        // 작성자와 현재 로그인한 사용자가 같은지 확인
        currentUserCheck(getPost(id), currentUserId);

        commentRepository.deleteByPost(getPost(id));
        postRepository.delete(getPost(id));
    }

    private User getUser(Long currentUserId) {
        return userRepository.findById(currentUserId).orElseThrow(NotFoundUserException::new);
    }

    private static void currentUserCheck(Post post, Long currentUserId) throws AccessDeniedException {
        if (!post.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }
    }

    private Post getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(NotFoundPostException::new);
        return post;
    }
}
