package com.cmc.mercury.global.response;

import lombok.Getter;

@Getter
public class SuccessResponse<T> {

    private final int code;
    private final String message;
    private T data;

    private SuccessResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> SuccessResponse<T> ok(T data) {
        return new SuccessResponse<>(200, "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> SuccessResponse<T> created(T data) {
        return new SuccessResponse<>(201, "리소스가 성공적으로 생성되었습니다.", data);
    }
}
