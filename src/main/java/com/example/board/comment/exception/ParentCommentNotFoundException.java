package com.example.board.comment.exception;

public class ParentCommentNotFoundException extends RuntimeException {
    public ParentCommentNotFoundException() {
        super("부모 댓글을 조회할 수 없습니다.");
    }

    public ParentCommentNotFoundException(String message) {
        super(message);
    }
}
