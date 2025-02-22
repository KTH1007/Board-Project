package com.example.board.post.exception;

public class NotFoundPostException extends RuntimeException {
    public NotFoundPostException() {
        super("해당하는 게시글을 찾을 수 없습니다,");
    }

    public NotFoundPostException(String message) {
        super(message);
    }
}
