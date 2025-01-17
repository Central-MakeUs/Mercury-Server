package com.cmc.mercury.global.response;

import com.cmc.mercury.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
//@RequiredArgsConstructor
@Schema(description = "공통 에러 응답 형식")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "Common400")
    private final String code;
    @Schema(description = "에러 메시지", example = "적절하지 않은 요청입니다.")
    private final String message;

    public ErrorResponse (ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}
