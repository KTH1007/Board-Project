package com.example.board.comment.api;

import com.example.board.comment.api.dto.request.CreateCommentRequest;
import com.example.board.comment.api.dto.request.UpdateCommentRequest;
import com.example.board.comment.api.dto.response.CommentResponse;
import com.example.board.comment.application.CommentService;
import com.example.board.global.security.CustomUserDetails;
import com.example.board.like.application.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @PostMapping("")
    @Operation(summary = "댓글 작성", description = "파라미터로 넘어온 값으로 댓글을 작성한다.")
    @ApiResponse(responseCode = "201", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponse> createComment(@Parameter(description = "게시글 id") @PathVariable Long postId,
                                                         @Parameter(description = "댓글 내용") @RequestBody @Valid CreateCommentRequest createCommentRequest,
                                                         Authentication authentication) throws AccessDeniedException {
        Long userId = getCurrentUserId(authentication);
        CommentResponse commentResponse = commentService.createComment(postId, createCommentRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @GetMapping("")
    @Operation(summary = "전체 댓글 조회", description = "모든 댓글을 조회한다")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<List<CommentResponse>> findAll(@Parameter(description = "게시글 id") @PathVariable Long postId) {
        List<CommentResponse> commentResponseList = commentService.findAll(postId);
        return ResponseEntity.ok(commentResponseList);
    }

    @GetMapping("/paging")
    @Operation(summary = "댓글 페이지로 조회", description = "모든 댓글을 조회한다")
    @ApiResponse(responseCode = "200", description = "성공")
    // 한 번에 10개씩 조회하며, 날짜 오름차순으로 정렬한다.
    public ResponseEntity<Page<CommentResponse>> getCommentsByPost(@Parameter(description = "게시글 id") @PathVariable Long postId,
                                                                   @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CommentResponse> commentResponseList = commentService.getCommentsByPost(postId, pageable);
        return ResponseEntity.ok(commentResponseList);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 id값과 내용으로 댓글을 수정한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponse> updateComment(@Parameter(description = "게시글 id") @PathVariable Long postId,
                                                         @Parameter(description = "댓글 id") @PathVariable Long commentId,
                                                         @Parameter(description = "댓글 내용") @RequestBody @Valid UpdateCommentRequest updateCommentRequest,
                                                         Authentication authentication) throws AccessDeniedException {
        Long userId = getCurrentUserId(authentication);
        CommentResponse commentResponse = commentService.updateComment(postId, commentId, updateCommentRequest, userId);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글 id값으로 댓글을 삭제한다.")
    @ApiResponse(responseCode = "204", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(@Parameter(description = "게시글 id") @PathVariable Long postId,
                                              @Parameter(description = "댓글 id") @PathVariable Long commentId,
                                              Authentication authentication) throws AccessDeniedException {
        Long userId = getCurrentUserId(authentication);
        commentService.deleteComment(postId, commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "댓글 추천", description = "댓글 id값과 유저 id 값으로 댓글을 추천한다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> likeComment(@Parameter(description = "게시글 id") @PathVariable Long postId,
                                            @Parameter(description = "댓글 id") @PathVariable Long commentId,
                                            Authentication authentication) throws AccessDeniedException {
        Long userId = getCurrentUserId(authentication);
        commentLikeService.likeComment(postId, userId, commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like")
    @Operation(summary = "댓글 추천 취소", description = "댓글 id값과 유저 id 값으로 댓글 추천을 취소한다.")
    @ApiResponse(responseCode = "204", description = "성공")
    @ApiResponse(responseCode = "400", description = "파라미터 오류")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> unlikeComment(@Parameter(description = "게시글 id") @PathVariable Long postId,
                                              @Parameter(description = "댓글 id") @PathVariable Long commentId,
                                              Authentication authentication) throws AccessDeniedException {
        Long userId = getCurrentUserId(authentication);
        commentLikeService.unlikeComment(postId, userId, commentId);
        return ResponseEntity.ok().build();
    }

    // 로그인한 사용자 ID 가져오기
    private Long getCurrentUserId(Authentication authentication) throws AccessDeniedException {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    }
}
