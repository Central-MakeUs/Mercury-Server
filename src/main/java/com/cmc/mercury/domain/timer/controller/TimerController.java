package com.cmc.mercury.domain.timer.controller;

import com.cmc.mercury.domain.timer.dto.TimerListResponse;
import com.cmc.mercury.domain.timer.dto.TimerRequest;
import com.cmc.mercury.domain.timer.dto.TimerResponse;
import com.cmc.mercury.domain.timer.service.TimerService;
import com.cmc.mercury.domain.user.entity.User;
import com.cmc.mercury.global.oauth.annotation.AuthUser;
import com.cmc.mercury.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timers")
@RequiredArgsConstructor
@Tag(name = "TimerController", description = "타이머 관련 API")
public class TimerController {

    private final TimerService timerService;

    @PostMapping
    @Operation(summary = "타이머 기록 등록", description = "타이머 기록을 생성합니다.")
    public SuccessResponse<TimerResponse> createTimer(
            @AuthUser User user,
            @RequestBody @Valid TimerRequest request
    ) {
        return SuccessResponse.created(
                timerService.createTimer(user, request)
        );
    }

    @GetMapping
    @Operation(summary = "타이머 기록 목록 조회", description = "타이머 기록 리스트를 반환합니다.")
    public SuccessResponse<TimerListResponse> getTimers(
            @AuthUser User user
    ) {
        return SuccessResponse.ok(
                timerService.getTimerList(user)
        );
    }
}
