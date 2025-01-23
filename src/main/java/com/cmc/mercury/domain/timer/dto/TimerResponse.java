package com.cmc.mercury.domain.timer.dto;

import com.cmc.mercury.domain.timer.entity.Timer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(title = "타이머 기록 생성 응답 형식")
public record TimerResponse(

        @Schema(description = "타이머 ID")
        Long timerId,
        @Schema(description = "타이머 기록 시간")
        int seconds,
        @Schema(description = "얻은 경험치")
        int acquiredExp,
        @Schema(description = "생성 일시")
        LocalDateTime createdAt,
        @Schema(description = "수정 일시")
        LocalDateTime updatedAt
) {

        public static TimerResponse of(Timer timer) {

                return new TimerResponse(
                        timer.getId(),
                        timer.getSeconds(),
                        timer.getAcquiredExp(),
                        timer.getCreatedAt(),
                        timer.getUpdatedAt()
                );
        }
}
