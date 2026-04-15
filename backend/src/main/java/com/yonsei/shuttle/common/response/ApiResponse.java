package com.yonsei.shuttle.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 API 응답의 통일된 포맷
 *
 * 성공: { "success": true, "data": {...} }
 * 실패: { "success": false, "error": { "code": "...", "message": "..." } }
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorBody error;

    // ===== 성공 응답 =====
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    // ===== 실패 응답 =====
    public static ApiResponse<Void> error(String code, String message) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorBody(code, message);
        return response;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorBody {
        private String code;
        private String message;

        ErrorBody(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}