package com.portai.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 던지는 예외.
 * 사용 예: throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
