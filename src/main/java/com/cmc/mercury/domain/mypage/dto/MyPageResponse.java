package com.cmc.mercury.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "마이페이지 응답 형식")
public record MyPageResponse(

        @Schema(description = "습관쌓기 ID")
        Long habitID,
        @Schema(description = "가입 기간")
        int joinDays,
        @Schema(description = "사용자 닉네임")
        String nickname,
        @Schema(description = "사용자 경험치")
        int exp,
        @Schema(description = "습관쌓기 연속 일수")
        int streakDays,
        @Schema(description = "최근 7일 streak 정보")
        List<WeeklyStreakResponse> weeklyStreak
        // @Schema(description = "가장 최근 독서기록 ID")
        // Long latestRecordID
) {

}
