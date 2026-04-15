package com.yonsei.shuttle.common.exception;

import lombok.Getter;

/**
 * 애플리케이션 비즈니스 예외
 * ErrorCode를 받아 일관된 에러 응답 생성
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}