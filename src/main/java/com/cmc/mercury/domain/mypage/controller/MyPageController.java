package com.cmc.mercury.domain.mypage.controller;

import com.cmc.mercury.domain.mypage.dto.HabitDetailResponse;
import com.cmc.mercury.domain.mypage.dto.MyPageResponse;
import com.cmc.mercury.domain.mypage.service.MyPageService;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.oauth.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
@Tag(name = "MyPageController", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("")
    @Operation(summary = "마이페이지 조회", description = "마이페이지 메인 화면을 조회합니다.")
    public ResponseEntity<MyPageResponse> getMyPage(@AuthUser User user) {
        return ResponseEntity.ok(myPageService.getMyPage(user));
    }

    @GetMapping("/history")
    @Operation(summary = "날짜별 습관쌓기 조회", description = "쿼리파라미터의 date 값은 YYYY-MM-DD 형식입니다.")
    public ResponseEntity<HabitDetailResponse> getHabitDetail(
            @AuthUser User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(myPageService.getHabitDetail(user, date));
    }
}
