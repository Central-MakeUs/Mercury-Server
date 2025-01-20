package com.cmc.mercury.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "Common400", "적절하지 않은 요청입니다."),
    PATH_VARIABLE_MISSING(HttpStatus.BAD_REQUEST, "Common400", "URL 경로 변수가 누락되었습니다."),
    QUERY_PARAM_MISSING(HttpStatus.BAD_REQUEST, "Common400", "필수 쿼리 파라미터가 누락되었습니다."),
    HEADER_MISSING(HttpStatus.BAD_REQUEST, "Common400", "필수 헤더가 누락되었습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "Common401", "인증이 필요합니다."),
    ACCESS_DENIED_ERROR(HttpStatus.FORBIDDEN, "Common403", "권한이 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Common404", "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Common405", "올바르지 않은 메서드입니다."),
    CONFLICT_ERROR(HttpStatus.CONFLICT, "Common409", "중복된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Common500", "서버 에러입니다."),

    // 도메인별
    // 알라딘 api
    ALADIN_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Aladin500", "알라딘 API 호출 실패");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
