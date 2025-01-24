package com.cmc.mercury.domain.user.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.UserTestService;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/test")
@RequiredArgsConstructor
@Tag(name = "UserTestController", description = "테스트 유저 생성 API")
public class UserTestController {

    private final UserTestService userTestService;

    @GetMapping("/get-or-create")
    public SuccessResponse<User> getOrCreateTestUser(@RequestParam Long userId) {

        return SuccessResponse.ok(userTestService.getOrCreateTestUser(userId));
    }

    @GetMapping()
    public SuccessResponse<List<User>> getAllUsers() {


        return SuccessResponse.ok(userTestService.getListUsers());
    }
}
