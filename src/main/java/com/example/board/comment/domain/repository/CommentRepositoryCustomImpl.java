package com.example.board.comment.domain.repository;

import com.example.board.comment.api.dto.response.CommentResponse;
import com.example.board.comment.domain.Comment;
import com.example.board.comment.domain.QComment;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.board.comment.domain.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentResponse> findByPostIdWithReplies(Long postId, Pageable pageable) {
        QComment child = new QComment("child");
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.childComments, child).fetchJoin() // 대댓글을 함께 조회
                .where(comment.post.id.eq(postId).and(comment.parentComment.isNull())) // 게시글 Id로 필터링, 부모 댓글만 조회
                .orderBy(comment.createdDate.asc()) // 작성일 기준 오름차순 정렬
                .offset(pageable.getOffset()) // 페이징 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();

        List<CommentResponse> content = comments.stream()
                .map(CommentResponse::toDto)
                .collect(Collectors.toList());

        // 전체 개수 조회 (페이징을 위한 카운트 쿼리)
        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Comment> findByPostIdWithReplies(Long postId) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.childComments).fetchJoin()
                .where(comment.post.id.eq(postId).and(comment.parentComment.isNull()))
                .orderBy(comment.createdDate.asc()) // 작성일 기준 오름차순 정렬
                .fetch();
    }
}
