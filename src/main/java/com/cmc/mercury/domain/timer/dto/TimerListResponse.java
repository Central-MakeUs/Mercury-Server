package com.cmc.mercury.domain.timer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "타이머 기록 목록 조회 응답 형식")
public record TimerListResponse(

        @Schema(description = "타이머 기록 목록")
        List<TimerResponse> timers
) {

}
