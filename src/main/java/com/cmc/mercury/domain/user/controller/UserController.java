package com.cmc.mercury.domain.user.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.response.UserTestRequest;
import com.cmc.mercury.domain.user.service.UserService;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "유저 관련 API")
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

    @GetMapping()
    public SuccessResponse<List<User>> getAllUsers() {

        return SuccessResponse.ok(userService.getListUsers());
    }
}
