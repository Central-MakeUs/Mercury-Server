package com.cmc.mercury.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;

@Schema(title = "날짜별 습관쌓기 응답 형식")
public record HabitDetailResponse(

        @Schema(description = "날짜별 습관쌓기 ID")
        Long habitHistoryID,
        @Schema(description = "요일")
        String day,
        @Schema(description = "습관쌓기 연속 일수")
        int streakCount,
        @Schema(description = "획득한 경험치")
        int acquiredExp,
        @Schema(description = "독서기록 또는 메모 작성 여부")
        boolean hasRecord,
        @Schema(description = "타이머 완료 여부")
        boolean hasTimer
) {

}
