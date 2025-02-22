package com.example.board.post.api;

import com.example.board.like.application.PostLikeService;
import com.example.board.post.api.dto.request.CreatePostRequest;
import com.example.board.post.api.dto.request.SearchPostRequest;
import com.example.board.post.api.dto.request.UpdatePostRequest;
import com.example.board.post.api.dto.response.PostResponse;
import com.example.board.post.apllication.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping("")
    @Operation(summary = "게시글 작성", description = "파라미터로 넘어온 값으로 게시글을 작성한다.")
    @ApiResponse(responseCode = "201", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<PostResponse> createPost(@Parameter(description = "게시글 제목, 내용, 카테고리") @RequestBody @Valid CreatePostRequest createPostRequest) {
        PostResponse postResponse = postService.createPost(createPostRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @GetMapping("")
    @Operation(summary = "게시글 리스트 조회", description = "모든 게시글을 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<List<PostResponse>> findAll() {
        List<PostResponse> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/paging")
    public ResponseEntity<Page<PostResponse>> list(@Parameter(description = "제목") @RequestParam(required = false) String title,
                                                   @Parameter(description = "내용") @RequestParam(required = false) String content,
                                                   Pageable pageable) {
        SearchPostRequest searchPostRequest = new SearchPostRequest(title, content);
        Page<PostResponse> page = postService.searchAndPagePost(searchPostRequest, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "파라미터의 id값으로 하나의 게시글을 조회한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<PostResponse> findPost(@Parameter(description = "게시글 id") @PathVariable Long id) {
        PostResponse postResponse = postService.findPost(id);
        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "파리미터로 넘어온 id값과 정보로 게시글을 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<PostResponse> updatePost(@Parameter(description = "게시글 id") @PathVariable Long id,
                                                   @Parameter(description = "게시글 제목, 내용, 카테고리") @RequestBody @Valid UpdatePostRequest updatePost) {
        PostResponse postResponse = postService.updatePost(id, updatePost);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "파라미터의 id값으로 하나의 게시글을 삭제한다.")
    @ApiResponse(responseCode = "204", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<Void> deletePost(@Parameter(description = "게시글 id") @PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 추천", description = "게시글 id와 유저 id 값으로 게시글을 추천한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<Void> likePost(@Parameter(description = "게시글 id") @PathVariable Long id, @Parameter(description = "유저 id") @RequestParam Long userId) {
        postLikeService.likePost(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/unlike")
    @Operation(summary = "게시글 추천 취소", description = "게시글 id와 유저 id 값으로 게시글 추천을 취소한다.")
    @ApiResponse(responseCode = "204", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    public ResponseEntity<Void> unlikePost(@Parameter(description = "게시글 id") @PathVariable Long id, @Parameter(description = "유저 id") @RequestParam Long userId) {
        postLikeService.unlikePost(userId, id);
        return ResponseEntity.ok().build();
    }
}
