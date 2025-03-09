package com.example.board.user.exception;

public class DuplicatedUserException extends RuntimeException {
    public DuplicatedUserException(String field, String value) {
        super(String.format("이미 존재하는 %s입니다.: %s", field, value));
    }
}
