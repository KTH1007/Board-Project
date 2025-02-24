package com.example.board.comment.domain;

import com.example.board.global.auditing.BaseEntity;
import com.example.board.post.domain.Post;
import com.example.board.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // 대댓글을 위한 자기 참조

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>(); // 대댓글 목록

    private int likeCount;

    @Version
    private Long version;

    @Builder
    public Comment(String content, Post post, User user, Comment parentComment) {
        this.content = content;
        this.post = post;
        this.user = user;
        this.parentComment = parentComment;
        this.likeCount = 0;
    }

    // 추천 수 증가 메서드
    public void increaseLikeCount() {
        this.likeCount++;
    }

    // 추천 수 감소 메서드
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
