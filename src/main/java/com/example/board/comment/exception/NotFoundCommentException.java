package com.example.board.comment.exception;

public class NotFoundCommentException extends RuntimeException {
    public NotFoundCommentException() {
        super("해당하는 댓글이 없습니다.");
    }

    public NotFoundCommentException(String message) {
        super(message);
    }
}
