package com.cmc.mercury.domain.user.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.response.UserTestRequest;
import com.cmc.mercury.domain.user.service.UserService;
import com.cmc.mercury.global.exception.CustomException;
import com.cmc.mercury.global.exception.ErrorCode;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "개발 환경에서의 유저 관련 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/test")
    @Operation(summary = "테스트 계정 생성", description = "개발 환경에서 사용할 테스트 계정을 생성합니다.")
    public SuccessResponse<User> createTestUser(@RequestBody @Valid UserTestRequest request) {

        return SuccessResponse.created(userService.createTestUser(request));
    }

    @DeleteMapping("/test")
    @Operation(summary = "테스트 계정 삭제", description = "개발 환경에서 사용한 테스트 계정을 삭제합니다.")
    public SuccessResponse<?> deleteTestUser(@RequestParam String email) {

        userService.deleteTestUser(email);
        return SuccessResponse.ok(new HashMap<>());
    }

    @PostMapping("/refresh/accessToken")
    @Operation(summary = "테스트 계정 토큰 재발급", description = "개발 환경에서 사용할 테스트 계정의 새로운 access 토큰을 발급합니다.")
    public SuccessResponse<User> refreshTestToken(@RequestBody UserTestRequest request) {

        return SuccessResponse.ok(userService.refreshTestToken(request));
    }

    @GetMapping("/me")
    @Operation(summary = "사용자 조회", description = "access token으로 사용자의 정보를 조회합니다.")
    public SuccessResponse<User> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {

        if (!StringUtils.hasText(authorizationHeader)) {
            throw new CustomException(ErrorCode.EMPTY_ACCESS_TOKEN);
        }

        // Bearer prefix 제거
        String accessToken = authorizationHeader;
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        return SuccessResponse.ok(userService.getUser(accessToken));
    }
}
