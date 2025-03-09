package com.example.board.user.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String field) {
        super(String.format("유효하지 않은 %s입니다.", field));
    }
}
