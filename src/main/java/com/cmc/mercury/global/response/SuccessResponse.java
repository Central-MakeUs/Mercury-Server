package com.cmc.mercury.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "공통 성공 응답 형식")
public class SuccessResponse<T> {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int code;
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;
    @Schema(description = "응답 데이터")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
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
