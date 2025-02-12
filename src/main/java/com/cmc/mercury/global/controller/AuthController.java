package com.cmc.mercury.global.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.jwt.JwtProvider;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "토큰 발급 API")
@Slf4j
public class AuthController {

    private final JwtProvider jwtProvider;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @PostMapping("/refresh")
    @Operation(summary = "refresh token 재발급", description = "access token 만료 시 refresh token을 통해 재발급을 요청합니다.")
    public SuccessResponse<?> refreshAccessToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {

        log.info("Refresh Token을 이용한 Access Token 갱신 요청");

        // Refresh Token 갱신
        String newRefreshToken = jwtProvider.refreshToken(refreshToken);

        // 새로운 Access Token 생성
        User user = jwtProvider.getUserFromToken(refreshToken);
        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());

        // 새로운 Access Token을 헤더에 추가
        response.setHeader("Authorization", "Bearer " + newAccessToken);

        // 새로운 Refresh Token을 쿠키에 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript에서 접근 방지
        // refreshTokenCookie.setSecure(true); // HTTPS만 허용
        refreshTokenCookie.setPath("/"); // 모든 경로에서 접근 가능
        // refreshTokenCookie.setDomain("mercuryplanet.co.kr");  // 도메인 간 쿠키 공유
        refreshTokenCookie.setMaxAge((int) refreshTokenValidity / 1000); // ms를 초 단위로 변환
        response.addCookie(refreshTokenCookie);

        return SuccessResponse.ok(new HashMap<>());
    }
}
