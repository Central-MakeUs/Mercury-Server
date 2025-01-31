package com.cmc.mercury.domain.timer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(title = "타이머 기록 생성 요청 형식")
public record TimerRequest(
        @NotNull(message = "기록 시간은 필수입니다.")
        @Schema(description = "기록 시간(단위: 초)")
        int seconds,

        @NotNull(message = "단말 시간은 필수입니다.")
        @Schema(description = "사용자 단말 시간 (ISO-8601 형식)",
                example = "2024-01-30T12:34:56")
        LocalDateTime deviceTime
) {

}
