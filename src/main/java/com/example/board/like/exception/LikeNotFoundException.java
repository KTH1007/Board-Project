package com.example.board.like.exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException() {
        super("해당 글을 추천하지 않았습니다.");
    }

    public LikeNotFoundException(String message) {
        super(message);
    }
}
