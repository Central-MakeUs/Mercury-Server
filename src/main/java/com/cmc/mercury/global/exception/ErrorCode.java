package com.cmc.mercury.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "Common400", "적절하지 않은 요청입니다."),
    REQUEST_BODY_MISSING(HttpStatus.BAD_REQUEST, "Common400", "올바르지 않은 요청 본문입니다."),
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
    ALADIN_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Aladin500", "알라딘 API 호출 실패"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User404", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "User409", "이미 존재하는 사용자입니다."),
    ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "User409", "이미 탈퇴한 사용자입니다."),

    // Book
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "Book404", "도서를 찾을 수 없습니다."),
    BOOK_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Book500", "도서 정보 저장에 실패했습니다."),

    // Record
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "Record404", "독서 기록 객체를 찾을 수 없습니다."),
    RECORD_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Record500", "독서 기록 객체 생성에 실패했습니다."),
    // RECORD_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Record500", "독서 기록 객체 삭제에 실패했습니다."),
    // UNAUTHORIZED_RECORD_ACCESS(HttpStatus.FORBIDDEN, "Record403", "해당 독서 기록 객체에 대한 접근 권한이 없습니다."),

    // RecordDetail
    RECORD_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "RecordDetail404", "독서 기록 상세를 찾을 수 없습니다."),
    // RECORD_DETAIL_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "RecordDetail500", "독서 기록 상세 생성에 실패했습니다."),

    // Memo
    MEMO_NOT_BELONG_TO_RECORD(HttpStatus.BAD_REQUEST, "Memo400", "해당 독서 기록 객체에 속한 메모가 아닙니다."),
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "Memo404", "메모를 찾을 수 없습니다."),
    // MEMO_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Memo500", "메모 생성에 실패했습니다.");

    // OAuth
    INVALID_OAUTH2_PROVIDER(HttpStatus.BAD_REQUEST, "OAuth400", "지원하지 않는 소셜 로그인 제공자입니다."),
    OAUTH2_PROCESSING_ERROR(HttpStatus.UNAUTHORIZED, "OAuth401", "소셜 로그인 처리 중 오류가 발생했습니다."),
    OAUTH2_LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "OAuth401", "소셜 로그인에 실패했습니다."),

    // Apple
    APPLE_CLIENT_SECRET_ERROR(HttpStatus.BAD_REQUEST, "Apple400", "Apple client secret 생성 실패"),
    APPLE_PRIVATE_KEY_ERROR(HttpStatus.BAD_REQUEST, "Apple400", "Apple private key 생성 실패"),
    APPLE_TOKEN_VALIDATION_ERROR(HttpStatus.UNAUTHORIZED, "Apple401", "Apple ID 토큰 검증 실패"),

    // JWT
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Jwt401", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Jwt401", "만료된 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Jwt401", "만료된 access 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Jwt401", "만료된 refresh 토큰입니다."),
    TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED, "Jwt401", "토큰 타입이 일치하지 않습니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "Jwt401", "토큰이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
