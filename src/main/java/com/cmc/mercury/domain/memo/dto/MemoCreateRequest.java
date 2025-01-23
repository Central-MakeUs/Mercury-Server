package com.cmc.mercury.domain.memo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(title = "메모 생성 요청 형식")
public record MemoCreateRequest(

        @Schema(description = "메모 내용", example = "시간 가는 줄 모르고 읽었다.")
        String content,

        @NotNull(message = "진도율은 필수입니다.")
        @Schema(description = "독서 진도율")
        int gauge
) {

}
