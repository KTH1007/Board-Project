package com.example.board.post.domain;

import com.example.board.global.auditing.BaseEntity;
import com.example.board.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int likeCount;

    @Version
    private Long version;

    @Builder
    public Post(String title, String content, Category category, User user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.likeCount = 0;
        this.user = user;
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

    public void updatePost(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
