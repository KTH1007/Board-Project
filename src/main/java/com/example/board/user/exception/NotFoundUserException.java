package com.example.board.user.exception;

public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException() {
        super("사용자가 존재하지 않습니다.");
    }

    public NotFoundUserException(String message) {
        super(message);
    }
}
