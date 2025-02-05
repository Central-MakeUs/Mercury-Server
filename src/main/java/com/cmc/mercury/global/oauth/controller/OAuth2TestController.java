package com.cmc.mercury.global.oauth.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.repository.UserRepository;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.oauth.annotation.AuthUser;
import com.cmc.mercury.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
//@RequestMapping("/login")
@Slf4j
@RequiredArgsConstructor
public class OAuth2TestController {

    private final UserRepository userRepository;

    @GetMapping("/login/success")
    public ResponseEntity<Map<String, String>> loginSuccess(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletRequest request) {

        Map<String, String> response = new HashMap<>();

        // Authorization 헤더에서 access token 확인
        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            response.put("accessToken", authHeader.substring(7));
//        }
//
//        // refresh token 확인
//        if (refreshToken != null) {
//            response.put("refreshToken", refreshToken);
//        }

        // 전달된 토큰 정보 로깅
        log.info("Access Token exists: {}", authHeader != null);
        log.info("Refresh Token exists: {}", refreshToken != null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/login/fail")
    public ResponseEntity<String> loginFail() {
        throw new CustomException(ErrorCode.OAUTH2_LOGIN_FAIL);
    }

    @GetMapping("/user/profile")
    public SuccessResponse<User> getProfile(@AuthUser User user) {
//        log.info("get Username: {}", userDetails.getUsername());
//        User user = userRepository.findById(Long.valueOf(userDetails.getUsername()))
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return SuccessResponse.ok(user);
    }
}
