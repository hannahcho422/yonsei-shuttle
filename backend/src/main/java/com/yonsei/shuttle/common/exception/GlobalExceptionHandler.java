package com.yonsei.shuttle.common.exception;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yonsei.shuttle.common.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리기
 * 모든 Controller에서 발생한 예외를 ApiResponse 포맷으로 변환
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 (CustomException)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("CustomException: {} - {}", errorCode.getCode(), e.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), e.getMessage()));
    }

    /**
     * @Valid 유효성 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errorMessage);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorMessage));
    }

    /**
     * 그 외 예상치 못한 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    private String formatFieldError(FieldError fieldError) {
        return "[" + fieldError.getField() + "] " + fieldError.getDefaultMessage();
    }
}