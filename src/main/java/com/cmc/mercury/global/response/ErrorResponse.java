package com.cmc.mercury.global.response;

import com.cmc.mercury.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
//@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

    public ErrorResponse (ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}
