package com.cmc.mercury.domain.user.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.SignOffService;
import com.cmc.mercury.global.oauth.annotation.AuthUser;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signoff")
@RequiredArgsConstructor
@Tag(name = "SignOffController", description = "로그아웃 & 탈퇴 API")
public class SignOffController {

    private final SignOffService signOffService;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃",
            description = "로그인한 사용자의 refresh token을 무효화시킵니다.(호출 후 프론트측의 access token을 무력화시켜주세요!)")
    public SuccessResponse<User> logout(@AuthUser User user, HttpServletRequest request, HttpServletResponse response) {

        User logoutUser = signOffService.logout(user);
        clearAuthContext(request, response);

        return SuccessResponse.ok(logoutUser);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "회원탈퇴(soft delete)",
            description = "로그인한 사용자의 refresh token을 무효화시키며 상태를 INAVTIVE로 바꿉니다.(호출 후 프론트측의 access token을 무력화시켜주세요!)")
    public SuccessResponse<User> withdraw(@AuthUser User user, HttpServletRequest request, HttpServletResponse response) {

        User deletedUser = signOffService.withdraw(user);
        clearAuthContext(request, response);

        return SuccessResponse.ok(deletedUser);
    }

    private void clearAuthContext(HttpServletRequest request, HttpServletResponse response) {

        // HttpSession & SecurityContext 초기화
        request.getSession().invalidate();  // 세션 초기화
        SecurityContextHolder.clearContext();  // Spring Security 인증 정보 제거

        // HttpOnly 쿠키 제거
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 즉시 삭제
        response.addCookie(cookie);
    }
}
