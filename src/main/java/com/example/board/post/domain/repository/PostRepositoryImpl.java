package com.example.board.post.domain.repository;

import com.example.board.post.api.dto.request.SearchPostRequest;
import com.example.board.post.api.dto.response.PostResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.board.post.domain.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostResponse> searchPage(SearchPostRequest searchPostRequest, Pageable pageable) {

        List<PostResponse> content = queryFactory
                // DTO로 변환할 필드 명시
                .select(Projections.constructor(PostResponse.class,
                        post.id,
                        post.title,
                        post.content,
                        post.category,
                        post.likeCount,
                        post.createdId,
                        post.updatedId,
                        post.createdDate,
                        post.updatedDate
                ))
                .from(post)
                .where(titleOrContentContains(searchPostRequest.title(), searchPostRequest.content()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(titleEq(searchPostRequest.title()), contentEq(searchPostRequest.content()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleEq(String title) {
        return StringUtils.hasText(title) ? post.title.eq(title) : null;
    }

    private BooleanExpression contentEq(String content) {
        return StringUtils.hasText(content) ? post.content.eq(content) : null;
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? post.title.contains(title) : null;
    }

    private BooleanExpression contentContains(String content) {
        return StringUtils.hasText(content) ? post.content.contains(content) : null;
    }

    // 제목 또는 내용의 부분 일치 처리
    private BooleanExpression titleOrContentContains(String title, String content) {
        BooleanExpression titleCondition = titleContains(title);
        BooleanExpression contentCondition = contentContains(content);

        // 둘 다 null이면 null 반환, 하나라도 조건이 있으면 or로 결합
        if (titleCondition == null && contentCondition == null) {
            return null;
        } else if (titleCondition == null) {
            return contentCondition;
        } else if (contentCondition == null) {
            return titleCondition;
        } else {
            return titleCondition.or(contentCondition);
        }
    }
}
