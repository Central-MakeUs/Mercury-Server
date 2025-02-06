package com.cmc.mercury.domain.user.controller;

import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.domain.user.service.UserService;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

//    @GetMapping("/test/")
//    public SuccessResponse<User> getOrCreateTestUser(@RequestParam Long testUserId) {
//
//        return SuccessResponse.ok(userTestService.getOrCreateTestUser(testUserId));
//    }

    @GetMapping()
    public SuccessResponse<List<User>> getAllUsers() {

        return SuccessResponse.ok(userService.getListUsers());
    }
}
