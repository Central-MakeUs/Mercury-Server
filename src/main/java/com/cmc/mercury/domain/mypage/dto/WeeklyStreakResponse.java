package com.cmc.mercury.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "요일별 성공 여부")
public record WeeklyStreakResponse(

        @Schema(description = "요일")
        String day,
        @Schema(description = "성공 여부")
        boolean isSuccess
) {

}
